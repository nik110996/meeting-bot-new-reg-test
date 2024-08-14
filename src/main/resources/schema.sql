
CREATE TABLE public.user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    profile_link VARCHAR(255),
    job VARCHAR(255),
    hobbie VARCHAR(255),
    age SMALLINT
);

CREATE TABLE public.chat (
    user_id BIGINT PRIMARY KEY,
    chat_state_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES public.user(id)
);

CREATE TABLE public.user_status (
    user_id BIGINT PRIMARY KEY,
    frozen BOOLEAN NOT NULL,
    banned BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES public.user(id)
);

CREATE TABLE public.meeting (
    meeting_id BIGINT PRIMARY KEY,
    meeting_date DATE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE public.final_meeting (
    final_meeting_id BIGINT PRIMARY KEY,
    final_meeting_date DATE NOT NULL,
    summary VARCHAR(255)
);
