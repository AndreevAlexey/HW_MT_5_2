import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 30000;
        String host = "127.0.0.1";
        Server server = new Server(host, port);
        // поток сервера
        Thread serverThread = new Thread(null, server::run);
        serverThread.start();
        // Определяем сокет сервера
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        final SocketChannel socketChannel = SocketChannel.open();
        // Получаем входящий и исходящий потоки информации
        try (socketChannel; Scanner scanner = new Scanner(System.in)) {
            // подключаемся к серверу
            socketChannel.connect(socketAddress);
            // Определяем буфер для получения данных
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            String msg;
            while (true) {
                // ввод строки с консоли
                System.out.println("Введите строку для редактирования(end для заврешения):");
                msg = scanner.nextLine();
                // проверка на выход
                if ("end".equals(msg)) break;
                // передача на сервер
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                Thread.sleep(2000);
                // ответ от сервера
                int bytesCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim());
                inputBuffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverThread.interrupt();
    }
}
