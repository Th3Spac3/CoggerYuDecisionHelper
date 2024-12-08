package MCD;

import java.util.Scanner;

public class Application {
    private Boolean exitFlag = false;
    public Application(){
        Start();
    }
    private void Start(){
        Scanner in = new Scanner(System.in);
        while(!exitFlag){
            ShowMainMenu();
            switch (in.nextInt()){
                case 1:
                    exitFlag = true;
                    break;
                case 2:
                    in.nextLine();
                    Settings(in);
                    break;
                case 3:
                    in.nextLine();
                    Work(in);
                    break;
                default:
                    InvalidCommand();
            }
        }
    }
    private void ShowMainMenu(){
        System.out.println("Введите номер команды:");
        System.out.println("1 - Выход");
        System.out.println("2 - Настройки ");
        System.out.println("3 - Ввод таблицы ");
    }
    private void InvalidCommand(){
        System.out.println("Введены неверные аргументы");
    }

    private void Work(Scanner in){
        System.out.println("Введите путь к таблице CSV или TXT");
        Object[][] table = CSVReader.ReadCSV(in.nextLine());
        String[] attributes = new String[table[0].length];
        for (int i = 0; i < attributes.length; i++) attributes[i] = table[0][i].toString();
        Double[][] values = new Double[table.length-1][table[0].length];
        for (int i = 0; i < values.length; i++)
            for (int j = 0; j < values[0].length; j++)
                values[i][j] = Double.parseDouble((String)table[i+1][j]);
        Ranker.Survey(values, attributes, in);
    }
    private void Settings(Scanner in){
        Boolean settingsExitFlag = true;
        while (settingsExitFlag){
            ShowSettings();
            ShowSettingsMenu();
            switch (in.nextInt()){
                case 1:
                    settingsExitFlag = false;
                    break;
                case 2:
                    in.nextLine();
                    Ranker.AdditionalInfo = !Ranker.AdditionalInfo;
                    break;
                case 3:
                    in.nextLine();
                    System.out.println("Введите основание шкалы предпочтения");
                    Ranker.P = in.nextDouble();
                    in.nextLine();
                    break;
                case 4:
                    if(Ranker.method == ChoiceMethod.CoggerYu) Ranker.method = ChoiceMethod.Simple;
                    else Ranker.method = ChoiceMethod.CoggerYu;
                default:
                    InvalidCommand();
            }
        }
    }
    private void ShowSettingsMenu(){
        System.out.println("Введите номер команды:");
        System.out.println("1 - Назад");
        System.out.println("2 - Вкл/выкл отображение дополнительной информации");
        System.out.println("3 - Выбор основания предпочтения");
        System.out.println("4 - Переключить метод ранжирования");
    }
    private void ShowSettings(){
        System.out.println("Показывать дополнительную информацию? "+ Ranker.AdditionalInfo);
        System.out.println("Текущее основание шкалы предпочтения: "+ Ranker.P);
        System.out.println("Метод ранжирования: "+ Ranker.method);
    }
}
