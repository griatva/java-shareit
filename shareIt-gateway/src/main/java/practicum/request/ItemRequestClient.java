package practicum.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import practicum.client.BaseClient;
import practicum.request.dto.ItemRequestDto;


@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(long requestorId, ItemRequestDto itemRequestDto) {
        return post("", requestorId, itemRequestDto);
    }

    public ResponseEntity<Object> getAllByRequestorIdWithSort(long requestorId) {
        return get("", requestorId);
    }

    public ResponseEntity<Object> getAllWithSort(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }
}