-- Stored SQL Procedures for 【B-501】IndexHistorical.asmx

-- Create Product
exec CreateProduct 40, 'Security Master', 'Webservice', 'IndexHistorical', 'IndexHistorical/All', 'IndexHistorical', 
N'指数銘柄のヒストリカルデータを提供します', 1, '-index-historical', '/product/-index-historical/', 
0, 0, 1, N'日本の指数銘柄に関するヒストリカルデータAPI'

update product set isactive = 1 where identifier = 'IndexHistorical'


-- Create Web Service
exec CreateWebService 'IndexHistorical', 1, 40, 'https/endpoint.com'


-- Set Produc Page
-- Create the Developer Resources page which automatically generates the API list and test form pages
exec SetProductPage 40, 'IndexHistorical', 1, 'DeveloperResources'

-- Create the Product Overview page that has the data coverage and other product information
-- The information is inserted using another stored procedure below called SetContent
exec SetProductPage 40, 'IndexHistorical', 1, 'ProductOverview'


declare @pid bigint
select @pid = productid from Product where Identifier = 'IndexHistorical' and Version = 1 and PlatformId = 40


-- Tags
insert into ProductTag
select @pid, tagid
from Tag
where tagname in ('Indices','CloudAPIs','Historical')


-- Set Content for the Product Overview page
-- Run this stored procedure multiple times to update product content
exec SetContent 40, 'IndexHistorical', 1, 'IndexHistorical Overview', N'null'
, 'ProductOverview'