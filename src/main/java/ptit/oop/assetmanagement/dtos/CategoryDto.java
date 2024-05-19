package ptit.oop.assetmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
@AllArgsConstructor
public class CategoryDto {
    private String prefix;
    private String category;
}
