package sda.hibernate;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
1.Głowne klasy:
    1.1 SessionFactory
    1.2 Session
2.Konfiguracja - hibernate.cfg.xml /persistance.xml
3.Logowanie zapytań
4.Generowanie schematu bazy i wypełnanie danymi
 */
public class Hibernate {

    public static void main(String[] args) throws InterruptedException {
        try (final SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Teacher.class)
                .addAnnotatedClass(Director.class)
                .addAnnotatedClass(InvoiceData.class)
                .addAnnotatedClass(Grade.class)
                .addAnnotatedClass(Academy.class)
                .buildSessionFactory()) {

            // selects(sessionFactory);

            // inserts(sessionFactory);

             // updates(sessionFactory);

            // delete(sessionFactory);

            nPlusSelectsProblemsSolvedWithFetch(sessionFactory);

        }

    }

    private static void nPlusSelectsProblemsSolvedWithFetch(SessionFactory sessionFactory) {
        System.out.println("Before many to many");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Student student1 = Student.builder().setFirstName("Ania")
                    .setLastName("Nowak")
                    .build();

            session.persist(student1);
            Grade grade = new Grade(1, student1);
            Grade grade1 = new Grade(3, student1);

            session.persist(grade);
            session.persist(grade1);

            Student student2 = Student.builder().setFirstName("Karol")
                    .setLastName("Nowak")
                    .build();

            session.persist(student2);
            Grade grade3 = new Grade(4, student2);
            Grade grade4 = new Grade(5, student2);

            session.persist(grade3);
            session.persist(grade4);

            Academy academy = new Academy("SDA", new HashSet<>(Arrays.asList(student1, student2)));

            session.persist(academy);

            transaction.commit();
        }
        System.out.println("Before student remove from academy");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Student student = session.find(Student.class, 7);
            Academy academy = session.find(Academy.class, "SDA");
            academy.getStudents().remove(student);

            transaction.commit();
        }

        try(Session session = sessionFactory.openSession()){
            List<Student> students = session.createQuery("SELECT distinct(s) FROM Student s LEFT JOIN FETCH s.grades", Student.class)
                    // /\ hibernatowy select wyciagnie wszystkie rekordy z tabeli student
            .getResultList();
            System.out.println("Students: " + students);

           Map<Integer, OptionalDouble> avgs = students.stream().collect(Collectors.toMap(Student::getId, calculateAvgGradeValue()));
            System.out.println("Students avgs: " + avgs);

        }
    }

    private static Function<Student, OptionalDouble> calculateAvgGradeValue() {
        return s -> s.getGrades().stream().map(Grade::getValue).mapToInt(i -> i).average();
    }

    private static void delete(SessionFactory sessionFactory) {
        System.out.println("Before delete");
        try (Session session = sessionFactory.openSession()) {
            Student student = session.find(Student.class, 1);
            Transaction transaction = session.beginTransaction();
            session.delete(student);
            transaction.commit();
            System.out.println("Student 1 deleted");

        }
    }

    private static void relationManyToOne(SessionFactory sessionFactory) {
        System.out.println("Before save student with grades");
        Student student1;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            student1 = Student.builder()
                    .setFirstName("Alicja")
                    .setLastName("Nowak")
                    // .setGrades(grades)
                    .build();
            session.persist(student1);
            Grade grade1 = new Grade(5, student1);
            Grade grade2 = new Grade(4, student1);
            session.persist(grade1);
            session.persist(grade2);
            transaction.commit();

        }
        System.out.println("After save student with grades. Student id: " + student1.getId());

        System.out.println("Before grades remove");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Student student2 = session.find(Student.class, student1.getId());
            System.out.println("Student grade count: " + student2.getGrades().size() + " student2 id: " + student2.getId());
            student2.getGrades().removeIf(g -> g.getId() == 1);
            //Grade grade = session.find(Grade.class, student2.getGrades().get(0).getId());
            // session.delete(grade);
            transaction.commit();

        }
        System.out.println("After grades remove");
    }

    private static void relationOneToOne(SessionFactory sessionFactory) {
        System.out.println("Before save student with invoice data");
        Student student;
        try (Session session = sessionFactory.openSession()) {
            InvoiceData invoiceData = new InvoiceData("123");
            Transaction transaction = session.beginTransaction();
            Address address = new Address("Wrocław", "Piłsudskiego");
            student = new Student("Paweł", "Kowalski", address, invoiceData);
            //session.persist(invoiceData); // załatwione przez CascadeType.PERSIST
            session.persist(student);
            transaction.commit();
            System.out.println("Student with invoice data saved");
        }

        try (Session session = sessionFactory.openSession()) {
            Student s = session.find(Student.class, student.getId());
            System.out.println("Student with invoice data: " + s);
            System.out.println("Invoice data: " + s.getInvoiceData());
        }
        Student s;
        try (Session session = sessionFactory.openSession()) {
            s = session.find(Student.class, student.getId());
            System.out.println("Student with invoice data: " + s);
        }
        //System.out.println("Invoice data: " + s.getInvoiceData()); - generuje wyjatek could not initialize proxy [sda.hibernate.InvoiceData#123] - no Session
        // dociaganie danych musi byc w obrebie sesji
    }



    private static void updates(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            System.out.println("Before update");
            Transaction transaction = session.beginTransaction();
            Student student = session.find(Student.class, 1);
            student.setAddress(new Address("Poznań", "Głogowska"));
            //session.update(student); update automatyczny wyołanie metody niepotrzebne

            transaction.commit();
        }

        Student studentAfterUpdate;
        try (Session session = sessionFactory.openSession()) {
            studentAfterUpdate = session.find(Student.class, 1);
            System.out.println("Student after update: " + studentAfterUpdate);
        }

        System.out.println("Before update in new session");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            studentAfterUpdate.setAddress(new Address("Kraków", "Mickiewicza"));
            session.update(studentAfterUpdate); // lub session.merge() jesli uzywamy JPA
            transaction.commit();
        }
    }

    private static void selects(SessionFactory sessionFactory) {
        inserts(sessionFactory);

        // select
        try(Session session = sessionFactory.openSession()) {
            System.out.println("Persist student and select");
            int studentId = 1;
            Transaction transaction = session.beginTransaction();
            Student studentToPersist = new Student("Adam","Adamowski",new Address("Sopot","Monciak"));
            session.persist(studentToPersist);
            Integer persistStudentId = studentToPersist.getId();
            System.out.println("Just persist student id " + persistStudentId);
            Optional<Student> student = Optional.ofNullable(session.find(Student.class,persistStudentId)); // pierwsze miejsce gdzie find zaglada jest session za pomoca persist


            System.out.println("Student with id: " + studentId + ": " + student.toString());

            transaction.commit();
        }
        //wyswietlenie post load
        try(Session session = sessionFactory.openSession()) {
            Student student = session.find(Student.class,3);

            System.out.println("Student 3: " + student);
        }


        try (Session session = sessionFactory.openSession()) {

            // https://javaee.github.io/javaee-spec/javadocs/javax/persistence/TypedQuery.html?is-external=true#getSingleResult--
            int studentId = 1;
            try {
                Query<Student> query = session.createQuery("SELECT s FROM Student s WHERE s.id = :studentId ", Student.class); // zapytanie HQL(jezyk hibernate) z parametrem, poslugujemy sie nazwa encji, w parametrach poslugujemy sie nazwami pol z klasy
                Student student1 = query
                        .setParameter("studentId", studentId)
                        .getSingleResult(); //jesli nic nie znajdzie rzuca wyjatkami

                System.out.println("Student from get single result" + student1);
            } catch (NoResultException e) {
                System.out.println("No results for id: " + studentId);
            }
        /*
        catch (NonUniqueResultException e )
        // w tym wypadku nie ma sensu bo odpytujemy po kluczu głównym wiec zawsze bedzie max jeden rezultat
            */
            String studentName = "Jan";
             Query<Student> query = session.createQuery("SELECT s FROM Student s WHERE s.firstName = :firstName ", Student.class);
             List<Student> students = query
                    .setParameter("firstName", studentName)
                     .setMaxResults(10)
                     .setFirstResult(0) // przydatne do miechanizow stronicowania
                     .getResultList();

            System.out.println("Student from get result list " + students);


            String studentName2 = "Jan";
            Query<Student> query2 = session.createQuery("SELECT s FROM Student s WHERE s.firstName = :firstName ", Student.class);
            Student students2 = query
                    .setParameter("firstName", studentName)
                    .setMaxResults(1)
                    .setFirstResult(0)
                    .getSingleResult();

            System.out.println("Student 2: " + students2);
        }

        //przyklad z native query, zapytania ze zwyklym SQL
        System.out.println("Before nativer query");
        try(Session session = sessionFactory.openSession()) {
          Object studentId = session.createNativeQuery("select name from tab_student where id = :studentId") // poslugujemy sie nazwami kolumn przy wyszukiwaniu
                    .setParameter("studentId",1)
                    .getSingleResult();

            System.out.println("Native query: " + studentId);
        }

        // konstrukcja, ktora pozwala na wybranie pol z encji z zapakowaniem
        try(Session session = sessionFactory.openSession()) {
            Query<StudentDTO> query2 = session.createQuery("SELECT new sda.hibernate.StudentDTO (s.firstName, s.lastName)" + "FROM Student s WHERE s.firstName = :firstName ", StudentDTO.class)
                    .setParameter("firstName","Jan");

            List<StudentDTO> studentDTOS = query2.getResultList();

            System.out.println("Students dtos: " + studentDTOS);

        }
        // zdefiniowane zapytania, zwieksza czytelnosc, konstrukcje tak tworzone są bezpieczne dla aplikacji, jego poprawnosc jest sprawdzana w momencie tworzenia session factory, blad wyrzuca od razu. WADA czas startu aplikacji, powoduje większe zużycie procesora na maszynie
        try(Session session = sessionFactory.openSession()) {
           Query<Student> namedQuery = session.createNamedQuery(Student.FIND_STUDENT_BY_FIRST_NAME,Student.class); // adnotacja @NamedQueries w klasie Student
                   List <Student> students = namedQuery
                    .setParameter("firstName","Jan")
                    .getResultList();

            System.out.println("Students from named query: " + students);
        }
    }

    private static void inserts(SessionFactory sessionFactory) {
        // Przyklady z insert

        Address address = new Address("Gdansk","Grunwaldzka");
        Student student = new Student("Jan", "Kowalski",address);
        System.out.println("Insert student 1");
        // przyklad 1
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();  // zeby wykonac cokolwiek w bazie trzeba pierwsze rozpoczac transkacje
            session.persist(student);  //zapis do bazy danych, zwraca void, metoda ze standardu JPA
            transaction.commit(); // zatwiedzenie do bazy danych

            System.out.println("Student added with id: " + student.getId());
        }
        Address address2 = new Address("Warszawa","Wiejska");
        Student student2 = new Student("Andrzej", "Nowak",address2);
        System.out.println("Insert student 2");
        // przyklad 2
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Serializable serializable = session.save(student2); //zwraca instancje serializable, metoda z API Hibernate
            transaction.commit();
            System.out.println("Student added with id: " + serializable);
            System.out.println("Student id class: " + serializable.getClass().getName());

        }
        System.out.println("Insert teacher 1");
        try(Session session = sessionFactory.openSession()) {
            Teacher teacher = new Teacher(new NamePk("Janina","Kowalska"));
            Teacher teacher2 = new Teacher(new NamePk("Janina","Nowak"));
            Teacher teacher3 = new Teacher(new NamePk("Janina","Nosowka"));
            Transaction transaction = session.beginTransaction();
            Serializable serializable = session.save(teacher);//zwraca instancje serializable, metoda z API Hibernate
            System.out.println("Insert teacher 2");
            session.save(teacher2);
            System.out.println("Insert teacher 3");
            session.save(teacher3);
            transaction.commit();
            System.out.println("Teacher added with id: " + serializable);
            System.out.println("Teacher id class: " + serializable.getClass().getName());

        }
    }

}
