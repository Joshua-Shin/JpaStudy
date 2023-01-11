package hellojpa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import jdk.jfr.Name;

@Entity
public class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    @Column(nullable = false, length = 10)
    private String name;
    private int price;
    private int stockQuantity;
}
