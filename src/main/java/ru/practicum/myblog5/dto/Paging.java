package ru.practicum.myblog5.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Paging {
    private int page;
    private int size;
    private String search;
    private int pageCount;
    private int[] pageSizes;

    public int getOffset() {
        return (page - 1) * size;
    }
}
