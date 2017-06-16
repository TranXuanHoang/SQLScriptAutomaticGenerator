-- Stored SQL Procedures for 【B-301】EquityTick.asmx

-- Create Product
exec CreateProduct 40, 'Security Master', 'Webservice', 'EquityTick', 'EquityTick/All', 'EquityTick', 
N'株式銘柄のTickデータを提供します', 1, '-equity-tick', '/product/equity-tick/', 
0, 0, 1, N'日本の株式銘柄に関するTickデータAPI'

update product set isactive = 1 where identifier = 'EquityTick'


-- Create Web Service
exec CreateWebService 'EquityTick', 1, 40, 'https/endpoint.com'


-- Set Produc Page
-- Create the Developer Resources page which automatically generates the API list and test form pages
exec SetProductPage 40, 'EquityTick', 1, 'DeveloperResources'

-- Create the Product Overview page that has the data coverage and other product information
-- The information is inserted using another stored procedure below called SetContent
exec SetProductPage 40, 'EquityTick', 1, 'ProductOverview'


declare @pid bigint
select @pid = productid from Product where Identifier = 'EquityTick' and Version = 1 and PlatformId = 40


-- Tags
insert into ProductTag
select @pid, tagid
from Tag
where tagname in ('Equities','CloudAPIs','Ticks')


-- Set Content for the Product Overview page
-- Run this stored procedure multiple times to update product content
exec SetContent 40, 'EquityTick', 1, 'EquityTick Overview', N'null'
, 'ProductOverview'