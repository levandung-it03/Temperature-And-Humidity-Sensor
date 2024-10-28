package dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class Feed {
    private LocalDateTime created_at;
    private Long entry_id;
    private Double field1;
    private Double field2;
    private Integer field3;

    public Feed() {super();}

    public Feed(LocalDateTime created_at, Long entry_id, Double field1, Double field2, Integer field3) {
        this.created_at = created_at;
        this.entry_id = entry_id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Long getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(Long entry_id) {
        this.entry_id = entry_id;
    }

    public Double getField1() {
        return field1;
    }

    public void setField1(Double field1) {
        this.field1 = field1;
    }

    public Double getField2() {
        return field2;
    }

    public void setField2(Double field2) {
        this.field2 = field2;
    }

    public Integer getField3() {
        return field3;
    }

    public void setField3(Integer field3) {
        this.field3 = field3;
    }
}