CREATE DATABASE Project1;
USE Project1;

CREATE TABLE Restaurant (
    RestaurantID    INT PRIMARY KEY,
    Name            VARCHAR(100) NOT NULL,
    Street          VARCHAR(150),
    City            VARCHAR(100),
    ZipCode         VARCHAR(20),
    PhoneNumber     VARCHAR(25)
);

CREATE TABLE Restaurant_Table (
    TableNumber     INT PRIMARY KEY,
    Status          VARCHAR(30),
    Floor           INT,
    Section         VARCHAR(50),
    Seats           INT,
    RestaurantID    INT,
    FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MenuItem (
    ItemID          INT PRIMARY KEY,
    RestaurantID    INT NOT NULL,
    Price           DECIMAL(10, 2),
    Name            VARCHAR(100) NOT NULL,
    Availability    BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MenuItem_Category (
    ItemID          INT,
    Category        VARCHAR(50),
    PRIMARY KEY (ItemID, Category),
    FOREIGN KEY (ItemID) REFERENCES MenuItem(ItemID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Users (
    UserID          INT PRIMARY KEY,
    FName           VARCHAR(50) NOT NULL,
    MName           VARCHAR(50),
    LName           VARCHAR(50) NOT NULL,
    Username        VARCHAR(50) UNIQUE,
    Email           VARCHAR(100) UNIQUE,
    PhoneNumber     VARCHAR(25),
    Password        VARCHAR(255),
    UserType        ENUM('Customer', 'Employee') NOT NULL
);

CREATE TABLE Employee (
    EmployeeID      INT PRIMARY KEY,
    RestaurantID    INT NOT NULL,
    Salary          DECIMAL(12, 2),
    SupervisorID    INT,
    FOREIGN KEY (EmployeeID) REFERENCES Users(UserID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (SupervisorID) REFERENCES Employee(EmployeeID)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE Employee_Role (
    EmployeeID      INT,
    Role            VARCHAR(50),
    PRIMARY KEY (EmployeeID, Role),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Customer (
    CustomerID      INT PRIMARY KEY,
    FOREIGN KEY (CustomerID) REFERENCES Users(UserID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Reservation (
    ReservationID   INT PRIMARY KEY,
    CustomerID      INT NOT NULL,
    TableNumber     INT,
    Guests          INT,
    Status          VARCHAR(30),
    DateTime        DATETIME,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (TableNumber) REFERENCES Restaurant_Table(TableNumber)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `Order` (
    OrderID         INT PRIMARY KEY,
    CustomerID      INT NOT NULL,
    EmployeeID      INT,
    Status          VARCHAR(30),
    TotalAmount     DECIMAL(12, 2),
    OrderDate       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE OrderDetails (
    OrderID         INT,
    ItemID          INT,
    Quantity        INT NOT NULL DEFAULT 1,
    SpecialRequest  VARCHAR(255),
    PRIMARY KEY (OrderID, ItemID),
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ItemID) REFERENCES MenuItem(ItemID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Payment (
    PaymentID       INT PRIMARY KEY,
    OrderID         INT NOT NULL,
    Amount          DECIMAL(12, 2) NOT NULL,
    Method          VARCHAR(50),
    Date            DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- 
-- INSERTION

INSERT INTO Restaurant VALUES
(1, 'RHA', 'King Fahd Road', 'Riyadh', '12271', '+966-11-456-7890');

INSERT INTO Restaurant_Table VALUES
(1, 'Available', 1, 'Main Hall', 4, 1),
(2, 'Available', 1, 'Main Hall', 4, 1),
(3, 'Occupied', 1, 'Main Hall', 6, 1),
(4, 'Available', 1, 'Window', 2, 1),
(5, 'Reserved', 1, 'Window', 2, 1),
(6, 'Available', 2, 'VIP', 8, 1),
(7, 'Occupied', 2, 'VIP', 6, 1),
(8, 'Available', 2, 'Terrace', 4, 1),
(9, 'Available', 2, 'Terrace', 4, 1),
(10, 'Maintenance', 2, 'Terrace', 4, 1);

INSERT INTO MenuItem VALUES
(1, 1, 45.00, 'Salmon Nigiri', TRUE),
(2, 1, 55.00, 'Dragon Roll', TRUE),
(3, 1, 38.00, 'Chicken Teriyaki', TRUE),
(4, 1, 65.00, 'Wagyu Beef Teppanyaki', TRUE),
(5, 1, 42.00, 'Shrimp Tempura', TRUE),
(6, 1, 28.00, 'Miso Soup', TRUE),
(7, 1, 72.00, 'Sashimi Platter', TRUE),
(8, 1, 35.00, 'Vegetable Gyoza', TRUE),
(9, 1, 48.00, 'Spicy Tuna Roll', FALSE),
(10, 1, 25.00, 'Edamame', TRUE);

INSERT INTO MenuItem_Category VALUES
(1, 'Sushi'),
(1, 'Seafood'),
(2, 'Sushi'),
(2, 'Signature'),
(3, 'Main Course'),
(3, 'Chicken'),
(4, 'Main Course'),
(4, 'Beef'),
(5, 'Appetizer'),
(5, 'Seafood'),
(6, 'Soup'),
(7, 'Sashimi'),
(7, 'Seafood'),
(8, 'Appetizer'),
(8, 'Vegetarian'),
(9, 'Sushi'),
(9, 'Spicy'),
(10, 'Appetizer'),
(10, 'Vegetarian');

-- Insert for Employees
INSERT INTO Users VALUES
(1, 'Mohammed', 'Abdullah', 'Al-Rashid', 'malrashid', 'mohammed.rashid@rha.sa', '+966-50-123-4567', 'hashed_pwd_1', 'Employee'),
(2, 'Fatimah', 'ahmed', 'Al-Dosari', 'fdosari', 'fatimah.dosari@rha.sa', '+966-50-234-5678', 'hashed_pwd_2', 'Employee'),
(3, 'Khalid', 'Omar', 'Al-Mutairi', 'kmutairi', 'khalid.mutairi@rha.sa', '+966-50-345-6789', 'hashed_pwd_3', 'Employee'),
(4, 'Noura', 'Abdullah', 'Al-Harbi', 'nharbi', 'noura.harbi@rha.sa', '+966-50-456-7890', 'hashed_pwd_4', 'Employee'),
(5, 'Ahmed', 'Saleh', 'Al-Ghamdi', 'aghamdi', 'ahmed.ghamdi@rha.sa', '+966-50-567-8901', 'hashed_pwd_5', 'Employee'),
(6, 'Sara', 'Fahad', 'Al-Qahtani', 'sqahtani', 'sara.qahtani@rha.sa', '+966-50-678-9012', 'hashed_pwd_6', 'Employee'),
(7, 'Yusuf', 'Ibrahim', 'Al-Shehri', 'yshehri', 'yusuf.shehri@rha.sa', '+966-50-789-0123', 'hashed_pwd_7', 'Employee'),
(8, 'Layla', 'Saleh', 'Al-Zahrani', 'lzahrani', 'layla.zahrani@rha.sa', '+966-50-890-1234', 'hashed_pwd_8', 'Employee'),
(9, 'Omar', 'Fahad', 'Al-Otaibi', 'ootaibi', 'omar.otaibi@rha.sa', '+966-50-901-2345', 'hashed_pwd_9', 'Employee'),
(10, 'Hessa', 'khaled', 'Al-Subaie', 'hsubaie', 'hessa.subaie@rha.sa', '+966-50-012-3456', 'hashed_pwd_10', 'Employee'),
-- Insert for Customers
(11, 'Abdulaziz', NULL, 'Al-Saud', 'asaud', 'abdulaziz.saud@gmail.com', '+966-55-111-2222', 'cust_pwd_1', 'Customer'),
(12, 'Reema', 'Nasser', 'Al-Faisal', 'rfaisal', 'reema.faisal@outlook.sa', '+966-55-222-3333', 'cust_pwd_2', 'Customer'),
(13, 'Turki', NULL, 'Al-Dakhil', 'tdakhil', 'turki.dakhil@yahoo.com', '+966-55-333-4444', 'cust_pwd_3', 'Customer'),
(14, 'Maha', 'Saad', 'Al-Rasheed', 'mrasheed', 'maha.rasheed@gmail.com', '+966-55-444-5555', 'cust_pwd_4', 'Customer'),
(15, 'Faisal', NULL, 'Al-Hamad', 'fhamad', 'faisal.hamad@hotmail.com', '+966-55-555-6666', 'cust_pwd_5', 'Customer'),
(16, 'Lulwa', 'Ahmed', 'Al-Bassam', 'lbassam', 'lulwa.bassam@gmail.com', '+966-55-666-7777', 'cust_pwd_6', 'Customer'),
(17, 'Sultan', NULL, 'Al-Turki', 'sturki', 'sultan.turki@outlook.sa', '+966-55-777-8888', 'cust_pwd_7', 'Customer'),
(18, 'Dalal', 'Mohammed', 'Al-Jasser', 'djasser', 'dalal.jasser@yahoo.com', '+966-55-888-9999', 'cust_pwd_8', 'Customer'),
(19, 'Nawaf', NULL, 'Al-Anazi', 'nanazi', 'nawaf.anazi@gmail.com', '+966-55-999-0000', 'cust_pwd_9', 'Customer'),
(20, 'Ghada', 'Khalid', 'Al-Mojel', 'gmojel', 'ghada.mojel@hotmail.com', '+966-55-000-1111', 'cust_pwd_10', 'Customer');

INSERT INTO Employee VALUES
(1, 1, 18000.00, NULL),
(2, 1, 15000.00, 1),
(3, 1, 12000.00, 1),
(4, 1, 8500.00, 2),
(5, 1, 8500.00, 2),
(6, 1, 7500.00, 3),
(7, 1, 7500.00, 3),
(8, 1, 6500.00, 2),
(9, 1, 6500.00, 3),
(10, 1, 6000.00, 2);

INSERT INTO Employee_Role VALUES
(1, 'General Manager'),
(2, 'Floor Manager'),
(3, 'Head Chef'),
(4, 'Waiter'),
(5, 'Waiter'),
(6, 'Sushi Chef'),
(7, 'Line Cook'),
(8, 'Hostess'),
(9, 'Kitchen Assistant'),
(10, 'Cashier');

INSERT INTO Customer VALUES
(11),
(12),
(13),
(14),
(15),
(16),
(17),
(18),
(19),
(20);

INSERT INTO Reservation VALUES
(1, 11, 6, 6, 'Confirmed', '2025-11-27 19:00:00'),
(2, 12, 4, 3, 'Confirmed', '2025-11-27 20:00:00'),
(3, 13, 7, 5, 'Pending', '2025-11-28 19:30:00'),
(4, 14, 5, 2, 'Confirmed', '2025-11-28 21:00:00'),
(5, 15, 1, 4, 'Cancelled', '2025-11-26 19:00:00'),
(6, 16, 8, 4, 'Confirmed', '2025-11-29 20:00:00'),
(7, 17, 3, 6, 'Completed', '2025-11-25 19:00:00'),
(8, 18, 2, 3, 'Pending', '2025-11-29 19:30:00'),
(9, 19, 9, 4, 'Confirmed', '2025-11-30 20:30:00'),
(10, 20, 4, 2, 'Confirmed', '2025-11-30 19:00:00');

DELETE FROM Payment;
DELETE FROM OrderDetails;
DELETE FROM `Order`;

INSERT INTO `Order` VALUES
(1, 17, 4, 'Completed', 210.00, '2025-11-25 19:25:00'),  -- Sashimi(72×1) + Dragon(55×2) + Miso(28×1) = 210
(2, 17, 5, 'Completed', 88.00, '2025-11-25 20:10:00'),   -- Edamame(25×2) + Chicken(38×1) = 88
(3, 11, 4, 'Preparing', 258.00, '2025-11-26 19:15:00'),  -- Wagyu(65×2) + Sashimi(72×1) + Miso(28×2) = 258
(4, 13, 5, 'Pending', 132.00, '2025-11-26 19:45:00'),    -- Salmon(45×2) + Shrimp(42×1) = 132
(5, 15, 1, 'Pending', 355.00, '2025-11-24 20:00:00'),    -- Wagyu(65×3) + Dragon(55×2) + Edamame(25×2) = 355
(6, 12, 5, 'Served', 167.00, '2025-11-26 20:30:00'),     -- Chicken(38×2) + Gyoza(35×1) + Miso(28×2) = 167
(7, 18, 4, 'Completed', 80.00, '2025-11-23 19:30:00'),   -- Edamame(25×1) + Dragon(55×1) = 80
(8, 14, 5, 'Preparing', 228.00, '2025-11-26 21:00:00'),  -- Sashimi(72×2) + Shrimp(42×2) = 228
(9, 16, 4, 'Pending', 163.00, '2025-11-26 20:45:00'),    -- Salmon(45×3) + Miso(28×1) = 163
(10, 19, 5, 'Completed', 146.00, '2025-11-22 19:15:00'); -- Chicken(38×2) + Gyoza(35×2) = 146

INSERT INTO OrderDetails VALUES
(1, 7, 1, NULL),
(1, 2, 2, 'Less spicy'),
(1, 6, 1, NULL),
(2, 10, 2, NULL),
(2, 3, 1, 'Extra sauce'),
(3, 4, 2, 'Medium rare'),
(3, 7, 1, NULL),
(3, 6, 2, 'No green onion'),
(4, 1, 2, NULL),
(4, 5, 1, 'Extra crispy'),
(5, 4, 3, NULL),
(5, 2, 2, NULL),
(5, 10, 2, NULL),
(6, 3, 2, NULL),
(6, 8, 1, NULL),
(6, 6, 2, NULL),
(7, 10, 1, NULL),
(7, 2, 1, 'No avocado'),
(8, 7, 2, NULL),
(8, 5, 2, 'Light batter'),
(9, 1, 3, NULL),
(9, 6, 1, NULL),
(10, 3, 2, NULL),
(10, 8, 2, 'Extra dipping sauce');

INSERT INTO Payment VALUES
(1, 1, 210.00, 'Credit Card', '2025-11-25 20:45:00'),
(2, 2, 88.00, 'Mada', '2025-11-25 21:30:00'),
(3, 5, 355.00, 'Cash', '2025-11-24 21:15:00'),
(4, 7, 80.00, 'Apple Pay', '2025-11-23 20:45:00'),
(5, 10, 146.00, 'Mada', '2025-11-22 20:30:00'),
(6, 3, 258.00, 'Credit Card', '2025-11-26 20:00:00'),
(7, 6, 167.00, 'STC Pay', '2025-11-26 21:45:00'),
(8, 8, 228.00, 'Mada', '2025-11-26 22:30:00');

-- Insert new menu item
INSERT INTO MenuItem (ItemID, RestaurantID, Price, Name, Availability)
VALUES (11, 1, 25.00, 'Strawberry Ice Cream', TRUE);

-- Categorize it as Dessert
INSERT INTO MenuItem_Category (ItemID, Category)
VALUES (11, 'Dessert');

-- Increase price by 10% using IDs
UPDATE MenuItem
SET Price = Price * 1.10
WHERE ItemID IN (12);

DELETE FROM MenuItem_Category
WHERE ItemID = 12;

DELETE FROM MenuItem
WHERE ItemID = 12;

SELECT UserID, FName, LName
FROM Users
WHERE LName LIKE '%i';

SELECT ItemID, Name, Price
FROM MenuItem
WHERE Price BETWEEN 20 AND 50;

SELECT OrderID, EmployeeID, Status
FROM `Order`
WHERE EmployeeID IN (1, 5);


SELECT ReservationID, CustomerID, DateTime, Status
FROM Reservation
ORDER BY DateTime DESC;



SELECT EmployeeID, SupervisorID
FROM Employee
WHERE SupervisorID IS NULL;


SELECT
    MI.ItemID,
    MI.Name,
    SUM(OD.Quantity) AS TotalSold
FROM OrderDetails OD
JOIN MenuItem MI ON OD.ItemID = MI.ItemID
GROUP BY MI.ItemID, MI.Name
HAVING SUM(OD.Quantity) > 3;


SELECT *
FROM MenuItem NATURAL JOIN OrderDetails;

SELECT
    MI.ItemID,
    MI.Name,
    OD.OrderID,
    OD.Quantity
FROM MenuItem MI
LEFT JOIN OrderDetails OD ON MI.ItemID = OD.ItemID;

SELECT 
    E.EmployeeID,
    U.FName AS SupervisorFirstName,
    U.LName AS SupervisorLastName
FROM Employee E
JOIN Users U ON E.EmployeeID = U.UserID
WHERE EXISTS (
    SELECT 1
    FROM Employee S
    WHERE S.SupervisorID = E.EmployeeID
);


SELECT
    O.OrderID,
    (SELECT SUM(Quantity)
     FROM OrderDetails
     WHERE OrderID = O.OrderID) AS TotalItems
FROM `Order` O;


DELIMITER $$

CREATE FUNCTION CalculateTax(amount DECIMAL(12,2))
RETURNS DECIMAL(12,2)
DETERMINISTIC
BEGIN
    RETURN amount * 0.10;
END$$

DELIMITER ;

SELECT PaymentID, Amount, CalculateTax(Amount) AS Tax
FROM Payment;

DELIMITER $$

CREATE PROCEDURE GetItemsByCategory(IN cat VARCHAR(50))
BEGIN
    SELECT MI.ItemID, MI.Name, MC.Category
    FROM MenuItem MI
    JOIN MenuItem_Category MC ON MI.ItemID = MC.ItemID
    WHERE MC.Category = cat;
END$$

DELIMITER ;
CALL GetItemsByCategory('Appetizer');


CREATE TABLE PriceLog (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    ItemID INT,
    OldPrice DECIMAL(10,2),
    NewPrice DECIMAL(10,2),
    ChangeDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
DELIMITER $$

CREATE TRIGGER LogPriceChange
BEFORE UPDATE ON MenuItem
FOR EACH ROW
BEGIN
    IF NEW.Price <> OLD.Price THEN
        INSERT INTO PriceLog (ItemID, OldPrice, NewPrice)
        VALUES (OLD.ItemID, OLD.Price, NEW.Price);
    END IF;
END$$

DELIMITER ;

ALTER TABLE PriceLog
ADD CONSTRAINT FK_PriceLog_Item
FOREIGN KEY (ItemID) REFERENCES MenuItem(ItemID)
ON DELETE CASCADE;

UPDATE MenuItem
SET Price = Price * 1.10
WHERE ItemID = 3;

SELECT * FROM PriceLog;


CREATE VIEW OrderHeader AS
SELECT
    O.OrderID,
    U.FName AS CustomerFirstName,
    U.LName AS CustomerLastName,
    O.TotalAmount,
    O.OrderDate
FROM `Order` O
JOIN Customer C ON O.CustomerID = C.CustomerID
JOIN Users U ON C.CustomerID = U.UserID;
SELECT * FROM OrderHeader;



CREATE VIEW OrderItems AS
SELECT
    O.OrderID,
    MI.Name AS ItemName,
    OD.Quantity,
    OD.SpecialRequest
FROM `Order` O
JOIN OrderDetails OD ON O.OrderID = OD.OrderID
JOIN MenuItem MI ON OD.ItemID = MI.ItemID;
SELECT * FROM OrderItems;









SELECT H.OrderID, H.CustomerFirstName, H.CustomerLastName, H.TotalAmount,
H.OrderDate,  I.ItemName, I.Quantity, I.SpecialRequest
FROM OrderHeader H
JOIN OrderItems I ON H.OrderID = I.OrderID;






