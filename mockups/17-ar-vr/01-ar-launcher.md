# AR Session Launcher

> **Mục đích:** Pre-session setup cho AR: chọn template, calibrate camera, test connection.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Bookings]
        H2[Start AR Session]
        H1 --- H2
    end

    subgraph TEMPLATE[Choose template]
        T1[🎯 Yoga Pose Correction]
        T2[🍳 Cooking Step-by-Step]
        T3[🔧 Bike Repair Guide]
        T4[💄 Makeup Tutorial]
        T5[🎸 Instrument Practice]
        T6[🌱 Plant Care]
        T7[🗣️ Language Conversation]
        T1 --- T2
        T2 --- T3
        T3 --- T4
        T4 --- T5
        T5 --- T6
        T6 --- T7
    end

    subgraph CALIB[Camera calibration]
        CB1[(📷 Camera preview)]
        CB2[Move your phone slowly in a circle]
        CB3[Progress: 60%]
        CB4[Lighting: ⚠️ too dim — find brighter spot]
        CB1 --- CB2
        CB2 --- CB3
        CB3 --- CB4
    end

    subgraph DEVICES[Devices]
        D1[Front camera ☑]
        D2[Back camera ☐]
        D3[External webcam: Not connected]
        D4[Microphone: Built-in iPhone]
        D1 --- D2
        D2 --- D3
        D3 --- D4
    end

    subgraph TEST[Connection test]
        TE1[Bandwidth: 12 Mbps ✓]
        TE2[Latency: 45ms ✓]
        TE3[Other user: Sarah verified ready ✓]
        TE1 --- TE2
        TE2 --- TE3
    end

    subgraph START[Start]
        ST1[[Launch AR session →]]
        ST2[Or save template for next time]
        ST1 --- ST2
    end

    HEADER ==> TEMPLATE
    TEMPLATE --> CALIB
    CALIB --> DEVICES
    DEVICES --> TEST
    TEST --> START

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class ST1 primary
    class CB4 warning
```

**Templates:** Pre-built 3D models + anchor points per category.
**Calibration:** Detect lighting + camera FOV + tracking quality.
