-- Stored SQL Procedures for 【B-401】IndexRealTime.asmx

-- Create Product
exec CreateProduct 40, 'Security Master', 'Webservice', 'IndexRealtime', 'IndexRealtime/All', 'IndexRealtime', 
N'指数銘柄のリアルタイムデータを提供します', 1, '-index-realtime', '/product/-index-realtime/', 
0, 0, 1, N'日本の指数銘柄に関するリアルタイムデータAPI'

update product set isactive = 1 where identifier = 'IndexRealtime'


-- Create Web Service
exec CreateWebService 'IndexRealtime', 1, 40, 'https/endpoint.com'


-- Set Produc Page
-- Create the Developer Resources page which automatically generates the API list and test form pages
exec SetProductPage 40, 'IndexRealtime', 1, 'DeveloperResources'

-- Create the Product Overview page that has the data coverage and other product information
-- The information is inserted using another stored procedure below called SetContent
exec SetProductPage 40, 'IndexRealtime', 1, 'ProductOverview'


declare @pid bigint
select @pid = productid from Product where Identifier = 'IndexRealtime' and Version = 1 and PlatformId = 40


-- Tags
insert into ProductTag
select @pid, tagid
from Tag
where tagname in ('Indices','CloudAPIs','RealTime')


-- Set Content for the Product Overview page
-- Run this stored procedure multiple times to update product content
exec SetContent 40, 'IndexRealtime', 1, 'IndexRealtime Overview', N'null'
, 'ProductOverview'