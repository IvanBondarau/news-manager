ALTER ROLE news_manager SET search_path TO news_manager, public;

DELETE FROM user_role;
DELETE FROM news_manager.user;
DELETE FROM news_tag;
DELETE FROM news_author;
DELETE FROM news;
DELETE FROM tag;
DELETE FROM author;

INSERT INTO author VALUES(1000, 'default name', 'default surname');
INSERT INTO news VALUES(1001, 'news1', 'short text', 'long text', '2000-01-01', '2000-01-02');
INSERT INTO news VALUES(1002, 'news2', 'short text', 'long text', '2000-01-01', '2000-01-02');
INSERT INTO news VALUES(1003, 'news3', 'short text', 'long text', '2000-01-01', '2000-01-02');
INSERT INTO tag VALUES(1001, 'tag1');
INSERT INTO tag VALUES(1002, 'tag2');
INSERT INTO tag VALUES(1003, 'tag3');
INSERT INTO news_tag VALUES(1001, 1002);
INSERT INTO news_tag VALUES(1001, 1003);
INSERT INTO news_tag VALUES(1002, 1001);
INSERT INTO news_tag VALUES(1002, 1002);
INSERT INTO news_tag VALUES(1002, 1003);
INSERT INTO news_tag VALUES(1003, 1001);
INSERT INTO news_tag VALUES(1003, 1003);
INSERT INTO news_manager.user VALUES(1000, 'name', 'surname', 'login', 'password');
INSERT INTO user_role VALUES(1000, 'default role name');
INSERT INTO news VALUES(1000, 'title', 'short text', 'long text', '2000-01-01', '2000-01-02');
INSERT INTO tag VALUES(1000, 'tag name');
