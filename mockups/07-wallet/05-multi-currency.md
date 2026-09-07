# Multi-Currency Display

> **Mục đích:** User chọn currency để hiển thị giá throughout app.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Display Currency]
        H1 --- H2
    end

    subgraph CURRENT[Current]
        C1[Display: USD $]
        C2[Charging: USD via Stripe]
        C3[Exchange rate: 1 USD = 25,000 VND]
        C1 --- C2
        C2 --- C3
    end

    subgraph OPTIONS[Options]
        O1[(●) USD $ — US Dollar]
        O2[( ) VND ₫ — Vietnamese Dong]
        O3[( ) SGD S$ — Singapore Dollar]
        O4[( ) IDR Rp — Indonesian Rupiah]
        O5[( ) PHP ₱ — Philippine Peso]
        O6[( ) EUR € — Euro]
        O7[( ) JPY ¥ — Japanese Yen]
        O1 --- O2
        O2 --- O3
        O3 --- O4
        O4 --- O5
        O5 --- O6
        O6 --- O7
    end

    subgraph PPP[Note]
        N1[Premium subscription uses PPP pricing:<br/>• Vietnam: 249,000 VND (~$10)<br/>• Indonesia: 49,000 IDR (~$3)<br/>• Singapore: $9.90 SGD<br/>• Philippines: ₱499 (~$9)]
    end

    subgraph ACTIONS[Save]
        SA[[Save preference]]
    end

    HEADER ==> CURRENT
    CURRENT --> OPTIONS
    OPTIONS --> PPP
    PPP --> ACTIONS

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SA primary
```

**Backend canonical:** USD always. Conversion cached daily.
