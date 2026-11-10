package ua.edu.digitaldean.app;

import ua.edu.digitaldean.model.*;

public final class DigitalDeanApp {
    private DigitalDeanApp() {}

    public static void main(String[] args) {
        if (args.length != 9) {
            System.err.println("Використання: DigitalDeanApp <studentId> \"<fullName>\" <email> <completedCredits> <courseCode> \"<courseTitle>\" <ects> <year> <term>");
            return;
        }

        try {
            StudentId studentId = new StudentId(args[0]);
            String fullName = args[1];
            EmailAddress email = new EmailAddress(args[2]);
            int completedCredits = Integer.parseInt(args[3]);

            Course course = new Course(args[4], args[5], Integer.parseInt(args[6]));
            Semester semester = new Semester(Integer.parseInt(args[7]), Integer.parseInt(args[8]));

            Student student = new Student(studentId, fullName, email, completedCredits);
            Enrollment enrollment = new Enrollment(studentId, course, semester);

            System.out.println("Студент: " + student);
            System.out.println("Контакт: " + email.value());
            System.out.println("Реєстрація: " + enrollment.summary());

            student.completeCourse(course);
            System.out.println("Кредити після завершення дисципліни: " + student.completedCredits());

        } catch (NumberFormatException e) {
            System.err.println("Помилка: числові аргументи мають містити цілі числа.");
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка: " + e.getMessage());
        }
    }
}
