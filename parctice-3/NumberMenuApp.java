import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class NumberMenuApp {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;
    private static final int DEFAULT_CAPACITY = 10;
    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 50;

    public static void main(String[] args) {
        int[] capacityHolder = new int[]{DEFAULT_CAPACITY};
        int startIndex = parseCapacity(args, capacityHolder);
        if (startIndex < 0) {
            return;
        }

        int capacity = capacityHolder[0];
        int[] values = new int[capacity];
        int count = loadArguments(args, startIndex, values);
        if (count < 0) {
            return;
        }

        int successfulChanges = 0;
        System.out.println("Початкових значень: " + count);

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            boolean running = true;
            while (running) {
                printMenu();
                Integer choice = readIntInRange(scanner, "Оберіть пункт: ", 0, 8);
                if (choice == null) {
                    System.out.println("\nВвід завершено.");
                    break;
                }

                switch (choice) {
                    case 1 -> showValues(values, count);
                    case 2 -> {
                        int newCount = addValue(scanner, values, count);
                        if (newCount > count) {
                            count = newCount;
                            successfulChanges++;
                        }
                    }
                    case 3 -> printStatistics(values, count);
                    case 4 -> countOccurrences(scanner, values, count);
                    case 5 -> {
                        if (replaceValue(scanner, values, count)) {
                            successfulChanges++;
                        }
                    }
                    case 6 -> {
                        int newCount = deleteValue(scanner, values, count);
                        if (newCount < count) {
                            count = newCount;
                            successfulChanges++;
                        }
                    }
                    case 7 -> showRange(scanner, values, count);
                    case 8 -> sortAscending(values, count);
                    case 0 -> running = false;
                    default -> throw new IllegalStateException("Непередбачений пункт меню: " + choice);
                }
            }
        }

        System.out.println("Успішних змін: " + successfulChanges);
        System.out.println("Роботу програми завершено.");
    }

    // Завдання 1: Парсинг параметра capacity з args[0]
    static int parseCapacity(String[] args, int[] capacityHolder) {
        if (args.length == 0) {
            return 0;
        }
        String first = args[0].strip();
        String prefix = "";
        if (first.startsWith("-capacity=")) {
            prefix = "-capacity=";
        } else if (first.startsWith("capacity=")) {
            prefix = "capacity=";
        }

        if (prefix.isEmpty()) {
            return 0; // Немає параметра місткості, args[0] — це початкове значення
        }

        String valueStr = first.substring(prefix.length()).strip();
        if (valueStr.isEmpty()) {
            System.err.println("Помилка: місткість не задано.");
            return -1;
        }

        try {
            int cap = Integer.parseInt(valueStr);
            if (cap < MIN_CAPACITY || cap > MAX_CAPACITY) {
                System.err.println("Помилка: місткість має бути від 1 до 50.");
                return -1;
            }
            capacityHolder[0] = cap;
            return 1; // Аргумент місткості успішно зчитано, початкові дані починаються з індексу 1
        } catch (NumberFormatException e) {
            System.err.printf("Помилка: \"%s\" не є цілою місткістю.%n", valueStr);
            return -1;
        }
    }

    // Завантаження початкових елементів із урахуванням зсуву startIndex
    static int loadArguments(String[] args, int startIndex, int[] values) {
        int initialValuesCount = args.length - startIndex;
        if (initialValuesCount > values.length) {
            System.err.printf("Помилка: кількість початкових значень перевищує місткість %d.%n", values.length);
            return -1;
        }

        for (int i = 0; i < initialValuesCount; i++) {
            String text = args[startIndex + i].strip();
            if (text.isEmpty()) {
                System.err.printf("Помилка аргументу %d: порожнє значення.%n", i + 1);
                return -1;
            }
            try {
                int val = Integer.parseInt(text);
                if (val < MIN_VALUE || val > MAX_VALUE) {
                    System.err.printf("Помилка аргументу %d: значення має бути від %d до %d.%n", i + 1, MIN_VALUE, MAX_VALUE);
                    return -1;
                }
                values[i] = val;
            } catch (NumberFormatException e) {
                System.err.printf("Помилка аргументу %d: \"%s\" не є цілим числом.%n", i + 1, args[startIndex + i]);
                return -1;
            }
        }
        return initialValuesCount;
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1 - Показати значення");
        System.out.println("2 - Додати значення");
        System.out.println("3 - Показати статистику");
        System.out.println("4 - Порахувати входження");
        System.out.println("5 - Змінити значення");
        System.out.println("6 - Видалити значення");
        System.out.println("7 - Показати діапазон");
        System.out.println("8 - Сортувати за зростанням");
        System.out.println("0 - Завершити роботу");
    }

    static Integer readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                return null;
            }
            String line = scanner.nextLine().strip();
            if (line.isEmpty()) {
                System.out.println("Помилка: введіть ціле число.");
                continue;
            }
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("Помилка: значення має бути від %d до %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Помилка: введіть ціле число.");
            }
        }
    }

    static void showValues(int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }
        System.out.print("Значення: [");
        for (int index = 0; index < count; index++) {
            if (index > 0) {
                System.out.print(", ");
            }
            System.out.print(values[index]);
        }
        System.out.println("]");
    }

    static int addValue(Scanner scanner, int[] values, int count) {
        if (count == values.length) {
            System.out.println("Масив заповнено. Додавання неможливе.");
            return count;
        }
        Integer value = readIntInRange(scanner, "Введіть значення від 0 до 100: ", MIN_VALUE, MAX_VALUE);
        if (value == null) {
            System.out.println("Додавання скасовано: ввід завершено.");
            return count;
        }
        values[count] = value;
        count++;
        System.out.println("Значення додано. Кількість елементів: " + count);
        return count;
    }

    // Завдання 6: Розширена статистика
    static void printStatistics(int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }
        long sum = 0;
        int minimum = values[0];
        int maximum = values[0];
        int evens = 0;
        int odds = 0;

        for (int index = 0; index < count; index++) {
            int value = values[index];
            sum += value;
            if (value < minimum) minimum = value;
            if (value > maximum) maximum = value;
            if (value % 2 == 0) evens++;
            else odds++;
        }

        double average = (double) sum / count;
        int greaterOrEqualAvg = 0;
        for (int index = 0; index < count; index++) {
            if (values[index] >= average) {
                greaterOrEqualAvg++;
            }
        }

        System.out.printf(Locale.ROOT,
                "Кількість: %d%nСума: %d%nМінімум: %d%nМаксимум: %d%nСереднє: %.2f%nПарних: %d%nНепарних: %d%nНе менших за середнє: %d%n",
                count, sum, minimum, maximum, average, evens, odds, greaterOrEqualAvg);
    }

    static void countOccurrences(Scanner scanner, int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }
        Integer target = readIntInRange(scanner, "Значення для пошуку: ", MIN_VALUE, MAX_VALUE);
        if (target == null) {
            System.out.println("Пошук скасовано: ввід завершено.");
            return;
        }
        int occurrences = 0;
        for (int index = 0; index < count; index++) {
            if (values[index] == target) occurrences++;
        }
        System.out.printf("Значення %d трапляється %d раз(и).%n", target, occurrences);
    }

    // Завдання 2: Заміна елемента
    static boolean replaceValue(Scanner scanner, int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return false;
        }
        Integer pos = readIntInRange(scanner, "Введіть номер елемента від 1 до " + count + ": ", 1, count);
        if (pos == null) return false;
        Integer newValue = readIntInRange(scanner, "Введіть нове значення від 0 до 100: ", MIN_VALUE, MAX_VALUE);
        if (newValue == null) return false;

        int index = pos - 1;
        int oldValue = values[index];
        values[index] = newValue;
        System.out.printf("Елемент %d змінено: %d -> %d.%n", pos, oldValue, newValue);
        return true;
    }

    // Завдання 3: Видалення елемента
    static int deleteValue(Scanner scanner, int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return count;
        }
        Integer pos = readIntInRange(scanner, "Введіть номер елемента від 1 до " + count + ": ", 1, count);
        if (pos == null) return count;

        int index = pos - 1;
        for (int i = index; i < count - 1; i++) {
            values[i] = values[i + 1];
        }
        values[count - 1] = 0;
        count--;
        System.out.printf("Елемент %d видалено. Кількість елементів: %d.%n", pos, count);
        return count;
    }

    // Завдання 4: Вибірка за діапазоном
    static void showRange(Scanner scanner, int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }
        Integer min = readIntInRange(scanner, "Введіть нижню межу: ", MIN_VALUE, MAX_VALUE);
        if (min == null) return;
        Integer max = readIntInRange(scanner, "Введіть верхню межу: ", MIN_VALUE, MAX_VALUE);
        if (max == null) return;

        if (min > max) {
            System.out.println("Помилка: нижня межа перевищує верхню.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        int foundCount = 0;
        for (int i = 0; i < count; i++) {
            if (values[i] >= min && values[i] <= max) {
                if (foundCount > 0) sb.append(", ");
                sb.append(values[i]);
                foundCount++;
            }
        }

        if (foundCount > 0) {
            System.out.printf("У діапазоні [%d, %d]: [%s]%n", min, max, sb.toString());
        } else {
            System.out.printf("У діапазоні [%d, %d] значень немає.%n", min, max);
        }
    }

    // Завдання 5: Сортування за зростанням
    static void sortAscending(int[] values, int count) {
        if (count <= 1) {
            System.out.println("Масив відсортовано.");
            return;
        }
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (values[j] > values[j + 1]) {
                    int temp = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = temp;
                }
            }
        }
        System.out.println("Масив відсортовано.");
    }
}