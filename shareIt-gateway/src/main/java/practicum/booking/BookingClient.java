package practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import practicum.booking.dto.BookingDto;
import practicum.booking.enums.BookingState;
import practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long bookerId, BookingDto bookingDto) {
        return post("", bookerId, bookingDto);
    }

    public ResponseEntity<Object> approveOrRejectBooking(long ownerId, long bookingId, Boolean approved) {
        String url = "/" + bookingId + "?approved=" + approved;
        return patch(url, ownerId);
    }


    public ResponseEntity<Object> getById(long requesterId, long bookingId) {
        return get("/" + bookingId, requesterId);
    }

    public ResponseEntity<Object> getBookingsByBookerIdWithFilter(long bookerId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("?state={state}", bookerId, parameters);
    }

    public ResponseEntity<Object> getBookingsByOwnerIdWithFilter(long ownerId, BookingState state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("/owner?state={state}", ownerId, parameters);
    }

}