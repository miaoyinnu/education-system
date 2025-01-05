-- 先删除有外键依赖的表
DELETE FROM grade;
DELETE FROM course_selection;
DELETE FROM course;
DELETE FROM student;
DELETE FROM teacher;
DELETE FROM classroom;
DELETE FROM user;

-- 或者如果想重置自增ID，可以使用TRUNCATE（需要临时禁用外键检查）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE grade;
TRUNCATE TABLE course_selection;
TRUNCATE TABLE course;
TRUNCATE TABLE student;
TRUNCATE TABLE teacher;
TRUNCATE TABLE classroom;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1; 