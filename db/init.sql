IF DB_ID(N'restaurant_auth') IS NULL
    CREATE DATABASE restaurant_auth;
GO

IF DB_ID(N'restaurant_db') IS NULL
    CREATE DATABASE restaurant_db;
GO

IF DB_ID(N'restaurant_reservation') IS NULL
    CREATE DATABASE restaurant_reservation;
GO

IF DB_ID(N'restaurant_payment') IS NULL
    CREATE DATABASE restaurant_payment;
GO
