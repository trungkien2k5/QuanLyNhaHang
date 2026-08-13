package com.kien.quanlynhahang.kafka;

import com.kien.quanlynhahang.event.OrderCreatedEvent;
import com.kien.quanlynhahang.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.annotation.BackOff;
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final MailService mailService;

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            topicSuffixingStrategy =
                    TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(
            topics = "order.created",
            groupId = "notification-group"
    )
    public void consume(OrderCreatedEvent event) {

        log.info(
                "Nhận ORDER_CREATED - maHD={}, maKH={}, tongTien={}",
                event.maHD(),
                event.maKH(),
                event.tongTien()
        );

        if (event.email() == null || event.email().isBlank()) {
            log.warn(
                    "Khách hàng không có email - maHD={}",
                    event.maHD()
            );
            return;
        }

        NumberFormat formatter =
                NumberFormat.getInstance(new Locale("vi", "VN"));

        String tongTienFormat =
                formatter.format(event.tongTien());

        String html = """
                <h2>🍽 Nhà hàng 5 sao Hà Nội</h2>
                <hr>

                <p>Xin chào <b>%s</b>,</p>

                <p>Hóa đơn của bạn đã được tạo thành công.</p>

                <table border="1" cellpadding="8" cellspacing="0">
                    <tr>
                        <td>Mã hóa đơn</td>
                        <td>%d</td>
                    </tr>

                    <tr>
                        <td>Tổng tiền</td>
                        <td>%s VNĐ</td>
                    </tr>

                    <tr>
                        <td>Trạng thái</td>
                        <td>Chưa thanh toán</td>
                    </tr>
                </table>

                <br>

                <p>Cảm ơn quý khách đã sử dụng dịch vụ!</p>
                """
                .formatted(
                        event.hoTen(),
                        event.maHD(),
                        tongTienFormat
                );

        mailService.guiMail(
                event.email(),
                "Xác nhận hóa đơn",
                html
        );

        log.info(
                "Đã gửi email xác nhận hóa đơn - maHD={}, email={}",
                event.maHD(),
                event.email()
        );
    }
    @DltHandler
    public void processDlt(OrderCreatedEvent event) {

        log.error(
                "EVENT ĐƯA VÀO DLT - maHD={}, maKH={}, tongTien={}",
                event.maHD(),
                event.maKH(),
                event.tongTien()
        );
    }
}