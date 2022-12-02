import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String path = "src\\subscribers.csv"; // Указываем путь к файлу subscribers (абоненты)
        File file = new File(path); // Определяем файл
        String[][] dataBase = new String[30][]; // Создаем базу данных абонентов
        float[] charges = new float[29]; // База начислений
        List<String> numberHouse = new ArrayList<>(); // Создаем список, в который будем помещать уникальные номера домов
        Scanner scanner; // Объявляем сканер с обработчиком исключений
        try {
            scanner = new Scanner(file); // Передаем в конструктор сканера файл
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line = "";
        for (int i = 0; i < dataBase.length; i++) { // В цикле пробегаемся по файлу абоненты и заносим информацию в базу данных
            line = scanner.nextLine();
            dataBase[i] = line.split(";");
        }
        file = new File("Accruals_subscribers.csv"); // Определяем файл "Начисления абоненты"
        PrintWriter out = new PrintWriter(file); // Передаем в конструктор принтера файл

        out.println("№ строки; Фамилия; Улица; № дома; № Квартиры; Тип начисления; Предыдущее; Текущее; Начислено"); // Задаем шапку файла "Начисления абоненты"

        for (int i = 1; i < dataBase.length; i++) { // Заносим информацию в файл "Начисления абоненты" (Accruals_subscribers)
            for (int j = 0; j <= 8; j++) {
                if (j == 8) {
                    int current = Integer.parseInt(dataBase[i][7]); // Получаем значение "Текущее" для i-ой строки
                    int previous = Integer.parseInt(dataBase[i][6]); // Получаем значение "Предыдущее" для i-ой строки
                    float res = (Integer.parseInt(dataBase[i][5])) == 1 ? 301.26f : (((current - previous) * 1.52f)); // Устанавливаем значение по нормативу (код 1) или высчитываем начисления по счетчику (код 2)
                    out.print(res + "р."); // Заносим результат в файл
                    charges[i - 1] = res; // Заносим результат в базу начислений
                }
                else out.print(dataBase[i][j] + "; ");
            }
            out.println();
            if (numberHouse.isEmpty()) numberHouse.add(dataBase[i][3]); // Добавляем первый уникальный номер дома
            else if (numberHouse.contains(dataBase[i][3])) continue; // Сравниваем номера домов, если одинаковые переходим к следующей итерации цикла
            else numberHouse.add(dataBase[i][3]); // Добавляем новый уникальный номер дома
        }
        out.close();

        file = new File("Аccruals_at_home");
        out = new PrintWriter(file);

        out.println("|№ строки  |Улица     |№ Дома    |Начислено |"); // Шапка таблицы файла "Начисления_дома"

        float[] houseCharges = new float[numberHouse.size()]; // Массив для записи начислений по дому
        String[][] street = new String[numberHouse.size()][]; // Массив для записи названия улиц и номера дома

         for (int i = 0; i < numberHouse.size(); i++) {
            for (int j = 0; j < charges.length; j++) {
                // Сравниваем уникальный номер дома с номером из базы данных, если номера совподают суммируем начисления
                if ((Integer.parseInt(numberHouse.get(i))) == Integer.parseInt(dataBase[j + 1][3])) {
                    houseCharges[i] += charges[j];
                    if (street[i] == null) street[i] = new String[]{dataBase[j + 1][2], dataBase[j + 1][3]}; // Записываем уникальные названия улиц номера домов
                }
                else continue;
            }
        }
        for (int i = 0; i < houseCharges.length; i++) { // Заносим результат в файл "Начисления_дома" (Acculars_at_home)
            out.println(formarCell(dataBase[i + 1][0]) + formarCell(street[i][0]) + formarCell(street[i][1]) + formarCell(houseCharges[i] + "") + "|");
        }
        out.close();
    }
    private static String formarCell (String line) { // Метод форматирует строку в виде таблицы
        String cell = "|" + line;
        for (int i = 0; i < (10 - line.length()); i++) cell += " ";
        return cell;
    }
}
