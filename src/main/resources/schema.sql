create table reservation
(
   id varchar not null,
   fromDate date not null,
   toDate date not null,
   email varchar not null,
   name varchar not null,
   primary key(id)
);