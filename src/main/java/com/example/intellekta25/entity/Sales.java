package com.example.intellekta25.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "sales")
public class Sales {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "date_good_incoming", nullable = false)
    private Date dateGoodIncoming;

    @Column(name = "date_sale", nullable = false)
    private Date dateSale;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product", referencedColumnName = "id")
    private Product product;

    public Sales() {

    }

    public Date getDateSale() {
        return dateSale;
    }

    public void setDateSale(Date dateSale) {
        this.dateSale = dateSale;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Date getDateGoodIncoming() {
        return dateGoodIncoming;
    }

    public void setDateGoodIncoming(Date dateGoodIncoming) {
        this.dateGoodIncoming = dateGoodIncoming;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Sales{" +
                "id=" + id +
                ", price=" + price +
                ", dateGoodIncoming=" + dateGoodIncoming +
                ", dateSale=" + dateSale +
                ", product=" + product +
                '}';
    }
}
