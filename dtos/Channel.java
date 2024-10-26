package dtos;

import java.time.LocalDateTime;

public class Channel {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long last_entry_id;

    public Channel() {super();}

    public Channel(Long id, String name, String description, Double latitude, Double longitude, String field1,
                   String field2, String field3, String field4, String field5, LocalDateTime created_at,
                   LocalDateTime updated_at, Long last_entry_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.last_entry_id = last_entry_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Long getLast_entry_id() {
        return last_entry_id;
    }

    public void setLast_entry_id(Long last_entry_id) {
        this.last_entry_id = last_entry_id;
    }
}