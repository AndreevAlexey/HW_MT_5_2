import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    final private String host;
    final private int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void run() {
        final ServerSocketChannel serverChannel;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(host, port));
            while(true) {
                if(Thread.interrupted()) {
                    break;
                }
                // Ждем подключения клиента и получаем потоки для дальнейшей работы
                try (SocketChannel socketChannel = serverChannel.accept()) {
                    // Определяем буфер для получения данных
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 3);
                    while (socketChannel.isConnected()) {
                        // читаем данные из канала в буфер
                        int bytesCount = socketChannel.read(inputBuffer);
                        // если из потока читать нельзя, перестаем работать с этим клиентом
                        if (bytesCount == -1) break;
                        // получаем переданную от клиента строку в нужной кодировке и очищаем буфер
                        String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        // редактирование строки
                        msg = msg.replaceAll(" ", "");
                        // отправляем сообщение клиента назад с пометкой ЭХО
                        socketChannel.write(ByteBuffer.wrap((msg).getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
