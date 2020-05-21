use seckilling;
drop table if exists user_info;
create table user_info
(
    id             int primary key auto_increment not null,
    name           varchar(64)                    not null,
    gender         tinyint                        not null,
    age            int                            not null,
    telephone      varchar(20)                    not null,
    register_mode  varchar(50)                    not null,
    third_party_id varchar(64)                    not null
);
drop table if exists user_password;
create table user_password
(
    id          int primary key auto_increment not null,
    encrypt_pwd varchar(64)                    not null,
    user_id     int                            not null,
    constraint foreign key (user_id) references user_info (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);
drop table if exists item_info;
create table item_info
(
    id          int primary key auto_increment,
    title       varchar(50)  not null default "",
    price       decimal      not null default 0,
    description varchar(500) not null default "",
    img_url     varchar(500) not null default "",
    sales       int          not null default 0
);
drop table if exists item_stock;
create table item_stock
(
    id      int primary key auto_increment,
    stock   int not null default 0,
    item_id int not null,
    constraint foreign key (item_id) references item_info (id)
        on delete restrict on update cascade
);
drop table if exists order_info;
create table order_info
(
    id          varchar(30) not null primary key default '',
    item_price  double      not null             default 0,
    total_price double      not null             default 0,
    amount      int         not null             default 0,
    user_id     int         not null             default 0,
    item_id     int         not null             default 0,
    constraint foreign key (item_id) references item_info (id)
        on delete restrict on update cascade,
    constraint foreign key (user_id) references user_info (id)
        on delete restrict on update cascade
);


drop table if exists sequence_info;
create table sequence_info
(
    name        varchar(20) not null default "",
    current_val int         not null default 0,
    step        int         not null default 1,
    max_value   int         not null default 99999,
    init_value  int         not null default 1
);

drop table if exists promo_info;
create table promo_info
(
    id         int primary key auto_increment,
    promo_name varchar(100) not null default "",
    start_date date         not null default "0000-00-00 00:00:00",
    end_date   date         not null default "0000-00-00 00:00:00",
    item_id    int          not null,
    sec_price  double       not null,
    constraint foreign key (item_id) references item_info (id)
        on update cascade on delete restrict
);
drop table if exists stock_log;
create table stock_log
(
    stock_log_id varchar(64) not null primary key,
    item_id      int         not null default 0,
    amount       int         not null default 0
)
