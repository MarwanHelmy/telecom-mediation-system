--
-- PostgreSQL database dump
--

\restrict QqHrKCkrjgcadgYy7EacVp7xl3LYwCyydVAlhPSfndlJIgYwFRBMpM7P6e6OSmL

-- Dumped from database version 16.11
-- Dumped by pg_dump version 16.11

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE IF EXISTS msdb;
--
-- Name: msdb; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE msdb WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF-8';


ALTER DATABASE msdb OWNER TO postgres;

\unrestrict QqHrKCkrjgcadgYy7EacVp7xl3LYwCyydVAlhPSfndlJIgYwFRBMpM7P6e6OSmL
\connect msdb
\restrict QqHrKCkrjgcadgYy7EacVp7xl3LYwCyydVAlhPSfndlJIgYwFRBMpM7P6e6OSmL

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: add_node(character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.add_node(p_name character varying, p_type character varying, p_protocol character varying, p_auth_type character varying, p_username character varying, p_password character varying, p_ip character varying, p_port integer, p_data_path character varying, p_archive_path character varying, p_isactive boolean) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_node_id INTEGER;
BEGIN

    INSERT INTO public.nodes
    (
        name,
        type,
        protocol,
        auth_type,
        username,
        password,
        ip,
        port,
        data_path,
        archive_path,
        isactive
    )
    VALUES
    (
        p_name,
        p_type,
        p_protocol,
        p_auth_type,
        p_username,
        p_password,
        p_ip,
        p_port,
        p_data_path,
        p_archive_path,
        p_isactive
    )
    RETURNING id
    INTO v_node_id;

    RETURN v_node_id;

END;
$$;


ALTER FUNCTION public.add_node(p_name character varying, p_type character varying, p_protocol character varying, p_auth_type character varying, p_username character varying, p_password character varying, p_ip character varying, p_port integer, p_data_path character varying, p_archive_path character varying, p_isactive boolean) OWNER TO postgres;

--
-- Name: admin_login(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.admin_login(p_username character varying, p_password character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN

    RETURN EXISTS
    (
        SELECT 1
        FROM public.admins
        WHERE username = p_username
        AND password = p_password
    );

END;
$$;


ALTER FUNCTION public.admin_login(p_username character varying, p_password character varying) OWNER TO postgres;

--
-- Name: check_duplicate_rule(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_duplicate_rule(p_source_id integer, p_destination_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    rule_exists boolean;
BEGIN
    SELECT EXISTS (
        SELECT 1 FROM routing_rules 
        WHERE source_node_id = p_source_id 
          AND destination_node_id = p_destination_id
    ) INTO rule_exists;
    
    RETURN rule_exists;
END;
$$;


ALTER FUNCTION public.check_duplicate_rule(p_source_id integer, p_destination_id integer) OWNER TO postgres;

--
-- Name: check_duplicate_rule_exclude_id(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_duplicate_rule_exclude_id(p_source_id integer, p_destination_id integer, p_exclude_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    rule_exists boolean;
BEGIN
    SELECT EXISTS (
        SELECT 1 FROM routing_rules 
        WHERE source_node_id = p_source_id 
          AND destination_node_id = p_destination_id
          AND id != p_exclude_id  
    ) INTO rule_exists;
    
    RETURN rule_exists;
END;
$$;


ALTER FUNCTION public.check_duplicate_rule_exclude_id(p_source_id integer, p_destination_id integer, p_exclude_id integer) OWNER TO postgres;

--
-- Name: delete_node(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_node(p_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN

    DELETE FROM nodes
    WHERE id = p_id;

END;
$$;


ALTER FUNCTION public.delete_node(p_id integer) OWNER TO postgres;

--
-- Name: delete_node_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_node_by_id(p_id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_name VARCHAR;
BEGIN

    
    SELECT name
    INTO v_name
    FROM public.nodes
    WHERE id = p_id;
    IF v_name IS NULL THEN
        RETURN NULL;
    END IF;
    DELETE FROM public.nodes
    WHERE id = p_id;

    
    RETURN v_name;

END;
$$;


ALTER FUNCTION public.delete_node_by_id(p_id integer) OWNER TO postgres;

--
-- Name: delete_rule(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_rule(p_rule_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    deleted_count integer;
BEGIN
    DELETE FROM routing_rules
    WHERE id = p_rule_id;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    
    RETURN deleted_count > 0;
END;
$$;


ALTER FUNCTION public.delete_rule(p_rule_id integer) OWNER TO postgres;

--
-- Name: delete_rules_on_type_change(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_rules_on_type_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN

    IF OLD.type <> NEW.type THEN

        DELETE FROM routing_rules
        WHERE source_node_id = NEW.id
           OR destination_node_id = NEW.id;

    END IF;

    RETURN NEW;

END;
$$;


ALTER FUNCTION public.delete_rules_on_type_change() OWNER TO postgres;

--
-- Name: get_active_nodes_count(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_active_nodes_count() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    active_count INTEGER;
BEGIN

    SELECT COUNT(*)
    INTO active_count
    FROM public.nodes
    WHERE isactive = true
    AND isdeleted = false;

    RETURN active_count;

END;
$$;


ALTER FUNCTION public.get_active_nodes_count() OWNER TO postgres;

--
-- Name: get_active_rules_count(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_active_rules_count() RETURNS integer
    LANGUAGE sql
    AS $$
    SELECT COUNT(*)
    FROM public.routing_rules
    WHERE is_active = true;
$$;


ALTER FUNCTION public.get_active_rules_count() OWNER TO postgres;

--
-- Name: get_all_nodes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_nodes() RETURNS TABLE(id integer, name character varying, type character varying, protocol character varying, auth_type character varying, username character varying, password character varying, ip character varying, port integer, data_path character varying, archive_path character varying, isactive boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN

    RETURN QUERY

    SELECT
        n.id,
        n.name,
        n.type,
        n.protocol,
        n.auth_type,
        n.username,
        n.password,
        n.ip,
        n.port,
        n.data_path,
        n.archive_path,
        n.isactive
    FROM public.nodes n
    WHERE n.isdeleted = false
    ORDER BY n.id;

END;
$$;


ALTER FUNCTION public.get_all_nodes() OWNER TO postgres;

--
-- Name: get_all_rules(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_all_rules() RETURNS TABLE(id integer, source_node_id integer, destination_node_id integer, is_active boolean, created_at timestamp without time zone)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT r.id, r.source_node_id, r.destination_node_id, r.is_active, r.created_at
    FROM routing_rules r
    ORDER BY r.id;
END;
$$;


ALTER FUNCTION public.get_all_rules() OWNER TO postgres;

--
-- Name: get_node_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_node_by_id(p_node_id integer) RETURNS TABLE(id integer, name character varying, type character varying, protocol character varying, auth_type character varying, username character varying, password character varying, ip character varying, port integer, data_path character varying, archive_path character varying, isactive boolean, isdeleted boolean)
    LANGUAGE plpgsql
    AS $$
BEGIN

    RETURN QUERY

    SELECT
        n.id,
        n.name,
        n.type,
        n.protocol,
        n.auth_type,
        n.username,
        n.password,
        n.ip,
        n.port,
        n.data_path,
        n.archive_path,
        n.isactive,
        n.isdeleted
    FROM public.nodes n
    WHERE n.id = p_node_id
    AND n.isdeleted = false;

END;
$$;


ALTER FUNCTION public.get_node_by_id(p_node_id integer) OWNER TO postgres;

--
-- Name: get_node_by_ip(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_node_by_ip(p_ip character varying) RETURNS TABLE(id integer, name character varying, ip character varying, port integer, type character varying, protocol character varying, username character varying, password character varying, isactive boolean, data_path character varying, archive_path character varying, auth_type character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN

    RETURN QUERY

    SELECT
        n.id,
        n.name,
        n.ip,
        n.port,
        n.type,
        n.protocol,
        n.username,
        n.password,
        n.isactive,
        n.data_path,
        n.archive_path,
        n.auth_type
    FROM public.nodes n
    WHERE n.ip = p_ip
    AND n.isdeleted = false;

END;
$$;


ALTER FUNCTION public.get_node_by_ip(p_ip character varying) OWNER TO postgres;

--
-- Name: get_upstream_nodes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_upstream_nodes() RETURNS TABLE(id integer, name character varying, type character varying, protocol character varying, auth_type character varying, username character varying, password character varying, ip character varying, port integer, data_path character varying, archive_path character varying)
    LANGUAGE plpgsql
    AS $$
BEGIN

    RETURN QUERY

    SELECT
        n.id,
        n.name,
        n.type,
        n.protocol,
        n.auth_type,
        n.username,
        n.password,
        n.ip,
        n.port,
        n.data_path,
        n.archive_path
    FROM public.nodes n
    WHERE n.type = 'UPSTREAM'
    AND n.isactive = true
    AND n.isdeleted = false
    ORDER BY n.id;

END;
$$;


ALTER FUNCTION public.get_upstream_nodes() OWNER TO postgres;

--
-- Name: insert_rule(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.insert_rule(p_source_id integer, p_destination_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN

    INSERT INTO routing_rules
    (
        source_node_id,
        destination_node_id
    )
    VALUES
    (
        p_source_id,
        p_destination_id
    );

END;
$$;


ALTER FUNCTION public.insert_rule(p_source_id integer, p_destination_id integer) OWNER TO postgres;

--
-- Name: insert_rule(integer, integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.insert_rule(p_source_id integer, p_destination_id integer, p_active boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    rule_exists boolean;
BEGIN

    SELECT EXISTS (
        SELECT 1 FROM routing_rules 
        WHERE source_node_id = p_source_id 
          AND destination_node_id = p_destination_id
    ) INTO rule_exists;
    

    IF rule_exists THEN
        RETURN false;
    END IF;
    

    INSERT INTO routing_rules (source_node_id, destination_node_id, is_active)
    VALUES (p_source_id, p_destination_id, p_active);
    
    RETURN true;
END;
$$;


ALTER FUNCTION public.insert_rule(p_source_id integer, p_destination_id integer, p_active boolean) OWNER TO postgres;

--
-- Name: is_node_ip_exists(character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.is_node_ip_exists(p_ip character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    ip_exists BOOLEAN;
BEGIN

    SELECT EXISTS
    (
        SELECT 1
        FROM public.nodes
        WHERE ip = p_ip
        AND isdeleted = false
    )
    INTO ip_exists;

    RETURN ip_exists;

END;
$$;


ALTER FUNCTION public.is_node_ip_exists(p_ip character varying) OWNER TO postgres;

--
-- Name: soft_delete_node(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.soft_delete_node(p_node_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    rows_affected INTEGER;
BEGIN

    UPDATE public.nodes
    SET isdeleted = true
    WHERE id = p_node_id
    AND isdeleted = false;

    GET DIAGNOSTICS rows_affected = ROW_COUNT;

    RETURN rows_affected > 0;

END;
$$;


ALTER FUNCTION public.soft_delete_node(p_node_id integer) OWNER TO postgres;

--
-- Name: update_node(integer, text, text, text, text, text, text, integer, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_node(p_id integer, p_name text, p_type text, p_protocol text, p_username text, p_password text, p_ip text, p_port integer, p_data_path text, p_archive_path text) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN

    UPDATE nodes
    SET
        name = p_name,
        type = p_type,
        protocol = p_protocol,
        username = p_username,
        password = p_password,
        ip = p_ip,
        port = p_port,
        data_path = p_data_path,
        archive_path = p_archive_path
    WHERE id = p_id;

END;
$$;


ALTER FUNCTION public.update_node(p_id integer, p_name text, p_type text, p_protocol text, p_username text, p_password text, p_ip text, p_port integer, p_data_path text, p_archive_path text) OWNER TO postgres;

--
-- Name: update_node_by_id(integer, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_node_by_id(p_id integer, p_name character varying, p_type character varying, p_protocol character varying, p_auth_type character varying, p_username character varying, p_password character varying, p_ip character varying, p_port integer, p_data_path character varying, p_archive_path character varying, p_isactive boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN

    UPDATE public.nodes
    SET
        name = p_name,
        type = p_type,
        protocol = p_protocol,
        auth_type = p_auth_type,
        username = p_username,
        password = p_password,
        ip = p_ip,
        port = p_port,
        data_path = p_data_path,
        archive_path = p_archive_path,
        isactive = p_isactive
    WHERE id = p_id
    AND isdeleted = false;

    IF FOUND THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;

END;
$$;


ALTER FUNCTION public.update_node_by_id(p_id integer, p_name character varying, p_type character varying, p_protocol character varying, p_auth_type character varying, p_username character varying, p_password character varying, p_ip character varying, p_port integer, p_data_path character varying, p_archive_path character varying, p_isactive boolean) OWNER TO postgres;

--
-- Name: update_node_status(integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_node_status(p_node_id integer, p_status boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN

    UPDATE public.nodes
    SET isactive = p_status
    WHERE id = p_node_id;

    IF FOUND THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;

END;
$$;


ALTER FUNCTION public.update_node_status(p_node_id integer, p_status boolean) OWNER TO postgres;

--
-- Name: update_rule(integer, integer, integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_rule(p_rule_id integer, p_source_id integer, p_destination_id integer, p_active boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    updated_count integer;
BEGIN
    UPDATE routing_rules
    SET
        source_node_id = p_source_id,
        destination_node_id = p_destination_id,
        is_active = p_active
    WHERE id = p_rule_id;
    
    GET DIAGNOSTICS updated_count = ROW_COUNT;
    
    RETURN updated_count > 0;
END;
$$;


ALTER FUNCTION public.update_rule(p_rule_id integer, p_source_id integer, p_destination_id integer, p_active boolean) OWNER TO postgres;

--
-- Name: validate_routing_rule(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validate_routing_rule() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    source_type TEXT;
    destination_type TEXT;
BEGIN

    SELECT type
    INTO source_type
    FROM nodes
    WHERE id = NEW.source_node_id;

    SELECT type
    INTO destination_type
    FROM nodes
    WHERE id = NEW.destination_node_id;

    IF source_type <> 'UPSTREAM' THEN

        RAISE EXCEPTION
        'SOURCE NODE MUST BE UPSTREAM';

    END IF;

    IF destination_type <> 'DOWNSTREAM' THEN

        RAISE EXCEPTION
        'DESTINATION NODE MUST BE DOWNSTREAM';

    END IF;

    RETURN NEW;

END;
$$;


ALTER FUNCTION public.validate_routing_rule() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: admins; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admins (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL
);


ALTER TABLE public.admins OWNER TO postgres;

--
-- Name: admins_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admins_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admins_id_seq OWNER TO postgres;

--
-- Name: admins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admins_id_seq OWNED BY public.admins.id;


--
-- Name: nodes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nodes (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    type character varying(20),
    protocol character varying(20) DEFAULT 'FTP'::character varying,
    auth_type character varying(20) DEFAULT 'password'::character varying,
    username character varying(50),
    password character varying(255),
    ip character varying(45) NOT NULL,
    port integer NOT NULL,
    data_path character varying(100),
    archive_path character varying(100),
    isactive boolean DEFAULT true,
    isdeleted boolean DEFAULT false,
    CONSTRAINT nodes_type_check CHECK (((type)::text = ANY ((ARRAY['UPSTREAM'::character varying, 'DOWNSTREAM'::character varying])::text[])))
);


ALTER TABLE public.nodes OWNER TO postgres;

--
-- Name: nodes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.nodes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.nodes_id_seq OWNER TO postgres;

--
-- Name: nodes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.nodes_id_seq OWNED BY public.nodes.id;


--
-- Name: routing_rules; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.routing_rules (
    id integer NOT NULL,
    source_node_id integer NOT NULL,
    destination_node_id integer NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.routing_rules OWNER TO postgres;

--
-- Name: routing_rules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.routing_rules_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.routing_rules_id_seq OWNER TO postgres;

--
-- Name: routing_rules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.routing_rules_id_seq OWNED BY public.routing_rules.id;


--
-- Name: admins id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins ALTER COLUMN id SET DEFAULT nextval('public.admins_id_seq'::regclass);


--
-- Name: nodes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes ALTER COLUMN id SET DEFAULT nextval('public.nodes_id_seq'::regclass);


--
-- Name: routing_rules id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routing_rules ALTER COLUMN id SET DEFAULT nextval('public.routing_rules_id_seq'::regclass);


--
-- Data for Name: admins; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admins (id, username, password) FROM stdin;
1	admin	123456
\.


--
-- Data for Name: nodes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nodes (id, name, type, protocol, auth_type, username, password, ip, port, data_path, archive_path, isactive, isdeleted) FROM stdin;
30	GSSN	UPSTREAM	SFTP	password	gssn	wqrq	172.30.0.30	45	cdrs	archive	f	f
27	MSC	UPSTREAM	FTP	password	msc	1133	172.30.0.10	15	ftp/cdrs	ftp/archive	f	f
28	SMSC	UPSTREAM	SFTP	password	smsc	214215	172.30.0.20	20	cdrs	archive	f	f
31	BILLING	DOWNSTREAM	SFTP	password	billing	2352	172.30.0.40	50	cdrs	archive	f	f
32	DWH	DOWNSTREAM	FTP	password	dwh	ewrR	172.30.0.60	60	ftp/cdrs	ftp/archive	f	f
\.


--
-- Data for Name: routing_rules; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.routing_rules (id, source_node_id, destination_node_id, is_active, created_at) FROM stdin;
12	27	32	t	2026-05-15 23:07:51.793862
14	30	32	t	2026-05-15 23:08:04.700109
13	28	32	t	2026-05-15 23:07:58.637592
15	27	31	t	2026-05-15 23:08:14.031093
\.


--
-- Name: admins_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.admins_id_seq', 1, true);


--
-- Name: nodes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.nodes_id_seq', 32, true);


--
-- Name: routing_rules_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.routing_rules_id_seq', 15, true);


--
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (id);


--
-- Name: admins admins_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_username_key UNIQUE (username);


--
-- Name: nodes nodes_ip_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_ip_key UNIQUE (ip);


--
-- Name: nodes nodes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nodes
    ADD CONSTRAINT nodes_pkey PRIMARY KEY (id);


--
-- Name: routing_rules routing_rules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routing_rules
    ADD CONSTRAINT routing_rules_pkey PRIMARY KEY (id);


--
-- Name: routing_rules uq_rule; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routing_rules
    ADD CONSTRAINT uq_rule UNIQUE (source_node_id, destination_node_id);


--
-- Name: nodes trg_delete_rules_on_type_change; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_delete_rules_on_type_change AFTER UPDATE ON public.nodes FOR EACH ROW EXECUTE FUNCTION public.delete_rules_on_type_change();


--
-- Name: routing_rules trg_validate_routing_rule; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_validate_routing_rule BEFORE INSERT OR UPDATE ON public.routing_rules FOR EACH ROW EXECUTE FUNCTION public.validate_routing_rule();


--
-- Name: routing_rules fk_destination_node; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routing_rules
    ADD CONSTRAINT fk_destination_node FOREIGN KEY (destination_node_id) REFERENCES public.nodes(id) ON DELETE CASCADE;


--
-- Name: routing_rules fk_source_node; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.routing_rules
    ADD CONSTRAINT fk_source_node FOREIGN KEY (source_node_id) REFERENCES public.nodes(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict QqHrKCkrjgcadgYy7EacVp7xl3LYwCyydVAlhPSfndlJIgYwFRBMpM7P6e6OSmL

