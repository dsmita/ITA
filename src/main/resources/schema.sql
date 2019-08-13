CREATE TABLE invoice ( 
	id                 identity GENERATED ALWAYS AS IDENTITY  NOT NULL,
	customerId         long   NOT NULL,
	subTotal           double   NOT NULL,
	tax                double   NOT NULL,
	total              double   NOT NULL,
	emailSent          boolean DEFAULT FALSE NOT NULL,
	createTime         timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updateTime         timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT PK_INVOICE_ID PRIMARY KEY ( id )
 );

CREATE TABLE product ( 
	id                 identity GENERATED ALWAYS AS IDENTITY  NOT NULL,
	name               varchar(255)   NOT NULL,
	price              double   NOT NULL,
	discount           double  DEFAULT 0,
	taxable            boolean  DEFAULT TRUE NOT NULL,
	createTime         timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
	updateTime         timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT PK_PRODUCT_ID PRIMARY KEY ( id )
 );

CREATE TABLE invoice_item ( 
	id                 identity GENERATED ALWAYS AS IDENTITY  NOT NULL,
	invoiceId          long   NOT NULL,
	productId          long   NOT NULL,
	quantity           integer   NOT NULL,
	tax                double   NOT NULL,
	discount           double DEFAULT 0,
	price              double   NOT NULL,
	CONSTRAINT PK_INVOICE_ITEM_ID PRIMARY KEY ( id )
 );

ALTER TABLE invoice_item ADD CONSTRAINT FK_INVOICE_ITEM_PRODUCT FOREIGN KEY ( PRODUCTID ) REFERENCES product( id ) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE invoice_item ADD CONSTRAINT FK_INVOICE_ITEM_INVOICE FOREIGN KEY ( invoiceId ) REFERENCES invoice( id ) ON DELETE NO ACTION ON UPDATE NO ACTION;