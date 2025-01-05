-- 先清理所有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE grade;
TRUNCATE TABLE course_selection;
TRUNCATE TABLE course;
TRUNCATE TABLE student;
TRUNCATE TABLE teacher;
TRUNCATE TABLE classroom;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 初始化用户数据
INSERT INTO user (id, username, password, role) VALUES
(1, 'admin', '123456', 'ADMIN'),
(2, 'teacher1', '123456', 'TEACHER'),
(3, 'teacher2', '123456', 'TEACHER'),
(4, 'teacher3', '123456', 'TEACHER'),
(5, 'student1', '123456', 'STUDENT'),
(6, 'student2', '123456', 'STUDENT'),
(7, 'student3', '123456', 'STUDENT'),
(8, 'student4', '123456', 'STUDENT');

-- 2. 初始化教师数据
INSERT INTO teacher (id, name, user_id) VALUES 
(1, '张三', 2),
(2, '李四', 3),
(3, '王五', 4);

-- 3. 初始化学生数据
INSERT INTO student (id, name, user_id, class_id) VALUES 
(1, '学生甲', 5, 1),
(2, '学生乙', 6, 1),
(3, '学生丙', 7, 2),
(4, '学生丁', 8, 2);

-- 4. 初始化教室数据
INSERT INTO classroom (id, name, capacity, building, type, status) VALUES 
(1, '101', 50, '教学楼A', '多媒体教室', '正常'),
(2, '102', 40, '教学楼A', '普通教室', '正常'),
(3, '201', 60, '教学楼A', '多媒体教室', '正常'),
(4, '202', 45, '教学楼A', '普通教室', '正常'),
(5, '301', 100, '教学楼B', '阶梯教室', '正常'),
(6, '302', 80, '教学楼B', '阶梯教室', '正常'),
(7, '401', 30, '实验楼', '计算机实验室', '正常'),
(8, '402', 30, '实验楼', '物理实验室', '正常');

-- 5. 初始化课程数据
INSERT INTO course (id, name, teacher_id, classroom_id, course_time, semester, max_students, current_students, credit) VALUES
(1, '高等数学', 1, 1, '周一 1-2节', '2024春季', 50, 1, 4),
(2, '大学物理', 2, 3, '周二 3-4节', '2024春季', 60, 0, 3),
(3, '计算机基础', 3, 7, '周三 5-6节', '2024春季', 30, 0, 2),
(4, '线性代数', 1, 2, '周四 1-2节', '2024春季', 40, 0, 4),
(5, '英语口语', 2, 4, '周五 3-4节', '2024春季', 45, 0, 4);

-- 6. 初始化选课数据
INSERT INTO course_selection (student_id, course_id) VALUES
(1, 1);

-- 7. 初始化成绩数据
INSERT INTO grade (student_id, course_id, score) VALUES
(1, 1, 85); 