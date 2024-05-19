package ptit.oop.assetmanagement.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse<T extends Serializable> {
    private Integer currentPage;
    private Integer totalItems;
    private Integer totalPages;
    private List<T> contents;
}
