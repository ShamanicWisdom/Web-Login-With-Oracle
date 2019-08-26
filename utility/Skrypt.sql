create table Logins
(Id NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) PRIMARY KEY ,
Login varchar(100) not null,
Password varchar(400) not null,
isBlocked int default 0 not null,
loginFailures int default 0 not null,
isArchival int default 0 not null,
DateBlocked timestamp default sysdate);

create table Passwords
(Id number,
Password varchar(400) not null,
DateInsert timestamp default sysdate,
FOREIGN KEY(Id) REFERENCES Logins(Id));

create table Ips
(Id number,
IP varchar(400) not null,
DateInsert timestamp default sysdate,
FOREIGN KEY(Id) REFERENCES Logins(Id));

create table Logs
(Id number,
DateLog timestamp default sysdate,
IP varchar(400) not null,
IdCorrect int default 1 not null,
FOREIGN KEY(Id) REFERENCES Logins(Id)
);


CREATE or replace PROCEDURE RemoveLogin (Log varchar, Pass varchar) AS
   BEGIN
      update Logins set isArchival = 1 where Login = Log and Password = Pass;
      commit;
   END;
   
create or replace PROCEDURE UpdatePassword (Log varchar, OldPass varchar, NewPass varchar) AS
   BEGIN
      update Logins set Password = NewPass where Login = Log and Password = OldPass;
      commit;
   END;
   
CREATE or replace PROCEDURE AddLogin (Log varchar, Pass varchar) AS
   BEGIN
      insert into Logins(Login, Password, isBlocked, loginFailures, isArchival) values(Log, Pass, 0, 0, 0);
      commit;
   END;
   
CREATE or replace PROCEDURE AddIp (Log varchar, Pass varchar, Ip varchar) AS
   BEGIN
      insert into Ips(Id, IP) values((select Id from Logins where Login = Log and Password = Pass),Ip);
      commit;
   END;
   
CREATE OR REPLACE function timestamp_difference(a timestamp, b timestamp) return number is 
begin
  return extract (day    from (a-b))*24*60+
         extract (hour   from (a-b))*60+
         extract (minute from (a-b));
end;

CREATE or replace PROCEDURE UpdateBlockade AS
   BEGIN
      update Logins set isblocked = 0, loginfailures = 0 where isblocked = 1 and (timestamp_difference(systimestamp, DateBlocked)) > 5;
   END;
   



CREATE OR REPLACE TRIGGER NUMBEROFLOGS 
AFTER INSERT ON LOGS 
REFERENCING OLD AS old NEW AS new 
FOR EACH ROW 


WHEN (new.IdCorrect = 0) 
BEGIN

   
   update Logins set loginFailures = (select loginFailures from Logins where Id = :new.Id) + 1 where Id = :new.Id;
   declare logg int;
   BEGIN
   select loginFailures into logg from Logins where Id = :new.Id;
   if logg >=3 then
    update Logins set isBlocked = 1, DateBlocked = sysdate where Id = :new.Id;
   end if;
   
   END;
   UpdateBlockade();
END;



CREATE OR REPLACE TRIGGER ZERONUMBEROFLOGS 
AFTER INSERT ON LOGS 
REFERENCING OLD AS old NEW AS new 
FOR EACH ROW 

WHEN (new.IdCorrect = 1) 
BEGIN
update Logins set loginFailures = 0 where Id = :new.Id;
END;




create or replace 
TRIGGER PasswordInsert 
AFTER INSERT OR UPDATE of Password ON Logins 
REFERENCING OLD AS old NEW AS new 
FOR EACH ROW 

BEGIN
   insert into Passwords(Id,Password) values(:new.id, :new.Password);
END;

-- Przykady u¿ycia procedur:

/*
call UpdatePassword('Admin', 'admin0','admino');
call RemoveLogin('Admin', 'admin0');
call AddLogin('Admin', 'admin0');
call AddIp('Admin', 'admin0');
call UpdateBlockade();
*/
