
package com.eventcommerce.orders.outbox;

import com.eventcommerce.orders.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop10ByStatusAndNextAttemptAtLessThanEqualOrderByIdAsc(
            String status,
            LocalDateTime now
    );
}

