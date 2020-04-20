# 秒杀项目构建过程

## day1
### 环境搭建
- 创建maven-archetype-quickstart工程
- 引入SpringBoot jar包
- [配置热部署](https://blog.csdn.net/qq_42685050/article/details/81588584)(调试方便，个人喜好)

### 引入Mybatis
- 引入MySQL数据库驱动
- 引入Druid数据库连接池
- 引入mybatis-spring-boot-starter
- 引入并配置```mybatis-generator```的plugin   
- [resources目录下建立```mybatis-generator.xml```](http://mybatis.org/generator/configreference/xmlconfig.html)
- 建立两张表```user表```和```user_password```
```mysql
create table user_info(
id int primary key auto_increment not null,
name varchar(64) not null,
gender tinyint not null,
age int not null,
telephone varchar(20) not null,
register_mode varchar(50) not null,
third_party_id varchar(64) not null);
```
```mysql
create table user_password(
id int primary key auto_increment not null,
encrpt_pwd varchar(64) not null,
user_id int not null,
constraint foreign key (user_id) references user_info(id) 
ON DELETE  RESTRICT  ON UPDATE CASCADE);
```
- 通过mybatis-generator自动生成数据库表对Java对象的映射
    1. 配置```mybatis-generator.xml```:
        1) 配置```jdbcConnection```字段,建立数据库连接
        2) 配置```javaModelGenerator```,生成实体类的存放位置
        3) 配置```sqlMapGenerator```,生成xml映射文件的存放位置
        4) 配置```javaClientGenerator```,生成dao接口类的存放位置 
    2. 执行命令```mybatis-generator:generate```,等待各类文件的自动生成,mysql驱动8.0版本会出现[Cannot obtain primary key information from the database, generated objects may be incomplete](http://m.aspku.com/view-326284.html)的WARNING,最好用5.x版本,或者在uri中加上nullCatalogMeansCurrent=true``
    
    

### 创建商品表
```mysql
create table item_info(
id int primary key auto_increment,
title varchar(50) not null default "",
price decimal not null default 0,
description varchar(500) not null default "",
img_url varchar(500) not null default "",
sales int not null default 0);
```     
```mysql
create table item_stock(
id int primary key auto_increment,
stock int not null default 0,
item_id int not null,
constraint foreign key (item_id) references item_info(id)
on delete restrict on update cascade
);
```

### 创建订单表

```mysql
drop table if exists order_info;
create table order_info(
id varchar(30) not null primary key default '',
item_price double not null default 0,
total_price double not null default 0,
amount int not null  default 0,
user_id int not null default 0,
item_id int not null default 0,
constraint foreign key (item_id) references item_info(id)
on delete restrict on update cascade ,
constraint foreign key (user_id) references user_info(id)
on delete restrict on update cascade 
);
```

### 创建sequence_info表辅助生成订单号
```mysql
create table sequence_info(
name varchar(20) not null default "",
current_val int not null default 0,
step int not null default 1,
max_value int not null default 99999,
init_value int not null default 1
);
```