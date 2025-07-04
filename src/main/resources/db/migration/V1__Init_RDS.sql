SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';
SET default_table_access_method = heap;

-- Proveedores
CREATE TABLE public.supplier (
    id uuid NOT NULL,
    name character varying(255) NOT NULL,
    nit character varying(50) NOT NULL,
    contact_name character varying(255) NOT NULL,
    contact_email character varying(255) NOT NULL,
    contact_phone character varying(50) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    active boolean DEFAULT true
);

-- Orden de compra a proveedor
CREATE TABLE public.supplierorder (
    id uuid NOT NULL,
    supplierid uuid NOT NULL,
    ordercode character varying(100) NOT NULL,
    orderdate date NOT NULL,
    expecteddeliverydate date NOT NULL,
    totalamount numeric(12,2) NOT NULL
);

-- Detalle de orden de compra
CREATE TABLE public.supplierorderdetail (
    id uuid NOT NULL,
    supplierorderid uuid NOT NULL,
    sku character varying(100) NOT NULL,
    quantityordered integer NOT NULL,
    unitprice numeric(10,2) NOT NULL
);

-- Recepción de pedido
CREATE TABLE public.supplierreceipt (
    id uuid NOT NULL,
    supplierorderid uuid NOT NULL,
    createdat timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    receivedby character varying(255) NOT NULL,
    notes text
);

-- Detalle de recepción
CREATE TABLE public.supplierreceiptdetail (
    id uuid NOT NULL,
    supplierreceiptid uuid NOT NULL,
    sku character varying(100) NOT NULL,
    quantityreceived integer NOT NULL
);

-- Llaves primarias y únicas
ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_nit_key UNIQUE (nit);

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.supplierorder
    ADD CONSTRAINT supplierorder_ordercode_key UNIQUE (ordercode);

ALTER TABLE ONLY public.supplierorder
    ADD CONSTRAINT supplierorder_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.supplierorderdetail
    ADD CONSTRAINT supplierorderdetail_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.supplierreceipt
    ADD CONSTRAINT supplierreceipt_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.supplierreceiptdetail
    ADD CONSTRAINT supplierreceiptdetail_pkey PRIMARY KEY (id);

-- Llaves foráneas
ALTER TABLE ONLY public.supplierorder
    ADD CONSTRAINT fk_supplierorder_supplier FOREIGN KEY (supplierid) REFERENCES public.supplier(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.supplierorderdetail
    ADD CONSTRAINT fk_supplierorderdetail_order FOREIGN KEY (supplierorderid) REFERENCES public.supplierorder(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.supplierreceipt
    ADD CONSTRAINT fk_supplierreceipt_order FOREIGN KEY (supplierorderid) REFERENCES public.supplierorder(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.supplierreceiptdetail
    ADD CONSTRAINT fk_supplierreceiptdetail_receipt FOREIGN KEY (supplierreceiptid) REFERENCES public.supplierreceipt(id) ON DELETE CASCADE;