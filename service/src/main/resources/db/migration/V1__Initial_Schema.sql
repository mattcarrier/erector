--
-- The MIT License
-- Copyright © 2016 Matt Carrier
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
--

CREATE TABLE PropertyGroup (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  version varchar(128) NOT NULL,
  status varchar(64) NOT NULL,
  CONSTRAINT uq_propertyGroup UNIQUE (name, version)
);

CREATE TABLE Property (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  key varchar(128) NOT NULL,
  value varchar(1024),
  description varchar(512),
  propertyGroupId BIGINT NOT NULL,
  CONSTRAINT uq_property UNIQUE (key, propertyGroupId),
  CONSTRAINT fk_property FOREIGN KEY (propertyGroupId) REFERENCES PropertyGroup(id)
);

CREATE TABLE FileMeta (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  location varchar(512) NOT NULL,
  propertyGroupId BIGINT NOT NULL,
  CONSTRAINT uq_fileMeta UNIQUE (name, propertyGroupId),
  CONSTRAINT fk_fileMeta FOREIGN KEY (propertyGroupId) REFERENCES PropertyGroup(id)
);

CREATE TABLE Tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  key varchar(128) NOT NULL,
  value varchar(512)
);

CREATE TABLE TagPropertyGroupXref (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tagId BIGINT NOT NULL,
  propertyGroupId BIGINT NOT NULL,
  CONSTRAINT uq_TagPropertyGroupXref UNIQUE (tagId, propertyGroupId),
  CONSTRAINT fk_TagPropertyGroupXref_PropertyGroup FOREIGN KEY (propertyGroupId) REFERENCES PropertyGroup(id),
  CONSTRAINT fk_TagPropertyGroupXref_Tag FOREIGN KEY (tagId) REFERENCES Tag(id)
);