
create table FX_RATES (
      CUR1  VARCHAR(3) PRIMARY KEY -- TODO: fix composite primary key
    , CUR2  VARCHAR(3) PRIMARY KEY
    , DESCR VARCHAR(255) NOT NULL
);
