package ru.yandex.myblog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Paging {
    private int page;
    private int size;
    private String search;
    private int pageCount;
    private int[] pageSizes;
}
