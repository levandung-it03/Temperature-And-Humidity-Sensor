package dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class Feed {
    private LocalDateTime created_at;
    private Long entry_id;
    private Double field1;
    private Double field2;
    private Double field3;
    private Double field4;
    private Integer field5;

    public Feed() {super();}

    public Feed(LocalDateTime created_at, Long entry_id, Double field1, Double field2, Double field3, Double field4,
                Integer field5) {
        this.created_at = created_at;
        this.entry_id = entry_id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
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

    public Double getField3() {
        return Objects.isNull(field3) ? 0.0 : field3;
    }

    public void setField3(Double field3) {
        this.field3 = field3;
    }

    public Double getField4() {
        return Objects.isNull(field4) ? 0.0 : field4;
    }

    public void setField4(Double field4) {
        this.field4 = field4;
    }

    public Integer getField5() {
        return Objects.isNull(field5) ? 0 : field5;
    }

    public void setField5(Integer field5) {
        this.field5 = field5;
    }
}