# SBT Mint Success

> **Mục đích:** Sau khi Skill Passport được issue, auto-mint SBT trên Polygon.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Profile]
        H2[🎉 Skill Passport Minted!]
        H1 --- H2
    end

    subgraph SUCCESS[Success celebration]
        S1[(🎉)]
        S2[Congratulations Mai!]
        S3[Your Skill Passport for Public Speaking<br/>has been minted as Soulbound Token on Polygon.]
        S1 --- S2
        S2 --- S3
    end

    subgraph SBT[SBT Details]
        BT1[🎯 Skill: Public Speaking]
        BT2[📊 Sessions: 24]
        BT3[⭐ Avg Rating: 4.7]
        BT4[📅 Issued: Sep 12 2026]
        BT5[⛓️ Network: Polygon Mainnet]
        BT6[📜 Contract: 0x1234...5678]
        BT7[🆔 Token ID: #42]
        BT8[🧱 Block: 51234567]
        BT9[⛽ Gas: 0.002 MATIC ~$0.001]
        BT1 --- BT2
        BT2 --- BT3
        BT3 --- BT4
        BT4 --- BT5
        BT5 --- BT6
        BT6 --- BT7
        BT7 --- BT8
        BT8 --- BT9
    end

    subgraph ACTIONS[What you can do now]
        A1[[🔗 Share passport public link]]
        A2[[💼 Add to LinkedIn]]
        A3[[🦊 View in MetaMask]]
        A4[[📋 Copy transaction hash]]
        A5[[📥 Download PDF certificate]]
        A1 --- A2
        A2 --- A3
        A3 --- A4
        A4 --- A5
    end

    subgraph USE[Use cases]
        U1[✓ Add to LinkedIn profile]
        U2[✓ Share with employers]
        U3[✓ Verify on-chain by 3rd parties]
        U4[✓ Build your verifiable credential portfolio]
        U1 --- U2
        U2 --- U3
        U3 --- U4
    end

    HEADER ==> SUCCESS
    SUCCESS --> SBT
    SBT --> ACTIONS
    ACTIONS --> USE

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef warning fill:#F59E0B,stroke:#D97706,color:#fff
    class A1,A2,A5 primary
    class SUCCESS warning
```

**Tech:** Web3j + Polygon RPC + IPFS (Pinata) cho metadata.
**Cost:** Mint ~$0.001 gas trên Polygon.
