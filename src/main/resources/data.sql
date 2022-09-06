insert into tag(tag_id, created_date, modified_date, name)
values (1, now(), now(), 'spring');
insert into tag(tag_id, created_date, modified_date, name)
values (2, now(), now(), 'java');
insert into tag(tag_id, created_date, modified_date, name)
values (3, now(), now(), 'c');
insert into tag(tag_id, created_date, modified_date, name)
values (4, now(), now(), 'c++');
insert into tag(tag_id, created_date, modified_date, name)
values (5, now(), now(), 'c#');
insert into tag(tag_id, created_date, modified_date, name)
values (6, now(), now(), 'javascript');
insert into tag(tag_id, created_date, modified_date, name)
values (7, now(), now(), 'typescript');
insert into tag(tag_id, created_date, modified_date, name)
values (8, now(), now(), 'react');
insert into tag(tag_id, created_date, modified_date, name)
values (9, now(), now(), 'vue');
insert into tag(tag_id, created_date, modified_date, name)
values (10, now(), now(), 'nodejs');
insert into tag(tag_id, created_date, modified_date, name)
values (11, now(), now(), 'python');
insert into tag(tag_id, created_date, modified_date, name)
values (12, now(), now(), 'django');
insert into tag(tag_id, created_date, modified_date, name)
values (13, now(), now(), 'go');
insert into tag(tag_id, created_date, modified_date, name)
values (14, now(), now(), 'swift');
insert into tag(tag_id, created_date, modified_date, name)
values (15, now(), now(), 'kotlin');
insert into tag(tag_id, created_date, modified_date, name)
values (16, now(), now(), 'angular');
insert into tag(tag_id, created_date, modified_date, name)
values (17, now(), now(), 'ruby');
insert into tag(tag_id, created_date, modified_date, name)
values (18, now(), now(), 'flutter');


insert into user (created_date, modified_date, auth_provider, nickname, role, social_login_id)
values (now(), now(), 'google', 'y1', 'USER', '123');
insert into user_tech_stack (created_date, modified_date, tag_id, user_id)
values (now(), now(), 2, 1);
insert into user_tech_stack (created_date, modified_date, tag_id, user_id)
values (now(), now(), 1, 1);



insert into post(created_date, modified_date, content, like_count, status, title, view_count,
                 user_id)
values (now(), now(), 'content1', 1, 'RECRUITING', 'title1', 1, 1);
insert into post(created_date, modified_date, content, like_count, status, title, view_count,
                 user_id)
values (now(), now(), 'content2', 1, 'RECRUITING', 'title2', 1, 1);


insert into POST_TECH_STACK(created_date, modified_date, tag_id, post_id)
values (now(), now(), 1, 1);
insert into POST_TECH_STACK(created_date, modified_date, tag_id, post_id)
values (now(), now(), 2, 2);

insert into LIKE_POST(created_date, modified_date, post_id, user_id, status)
values (now(), now(), 1, 1, 'ACTIVE');
insert into LIKE_POST(created_date, modified_date, post_id, user_id, status)
values (now(), now(), 1, 1, 'ACTIVE');
