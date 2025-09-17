package co.com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
}
