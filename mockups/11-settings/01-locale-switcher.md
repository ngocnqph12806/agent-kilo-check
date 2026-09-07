# Locale Switcher

> **Mục đích:** Đổi ngôn ngữ + currency + timezone.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Language & Region]
        H1 --- H2
    end

    subgraph LANG[Display language]
        LA1[🇻🇳 Tiếng Việt]
        LA2[🇺🇸 English]
        LA3[🇮🇩 Bahasa Indonesia]
        LA4[🇵🇭 Filipino]
        LA5[🇨🇳 中文 简体]
        LA6[🇩🇪 Deutsch]
        LA7[🇫🇷 Français]
        LA8[[+ More languages ▼]]
        LA1 --- LA2
        LA2 --- LA3
        LA3 --- LA4
        LA4 --- LA5
        LA5 --- LA6
        LA6 --- LA7
        LA7 --- LA8
    end

    subgraph CURRENCY[Currency]
        CU1[(●) USD $]
        CU2[( ) VND ₫]
        CU3[( ) SGD S$]
        CU4[( ) IDR Rp]
        CU5[( ) PHP ₱]
        CU6[( ) EUR €]
        CU7[( ) JPY ¥]
        CU8[Auto-detect from location]
        CU1 --- CU2
        CU2 --- CU3
        CU3 --- CU4
        CU4 --- CU5
        CU5 --- CU6
        CU6 --- CU7
        CU7 --- CU8
    end

    subgraph TZ[Timezone]
        T1[Asia/Ho_Chi_Minh GMT+7 ▼]
        T2[Auto-detect: ✓ enabled]
        T1 --- T2
    end

    subgraph DATEFMT[Date & number format]
        D1[Date: DD/MM/YYYY ▼]
        D2[Time: 24-hour ▼]
        D3[First day of week: Monday ▼]
        D1 --- D2
        D2 --- D3
    end

    subgraph SAVE[Save]
        SV1[[Save preferences]]
        SA[Some changes may require app restart]
        SV1 --- SA
    end

    HEADER ==> LANG
    LANG --> CURRENCY
    CURRENCY --> TZ
    TZ --> DATEFMT
    DATEFMT --> SAVE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class SV1 primary
```

**Persistence:** Save to user profile + cookies for anon sessions.
**Translation coverage:** Hiển thị % coverage mỗi locale.
