# Connect Web3 Wallet

> **Mục đích:** Connect MetaMask hoặc WalletConnect để mint SBT.

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Settings]
        H2[Connected Wallets]
        H1 --- H2
    end

    subgraph HERO[Hero]
        T1[Connect your Web3 wallet]
        T2[Mint Soulbound Tokens for your Skill Passports<br/>and verify credentials with third parties.]
        T1 --- T2
    end

    subgraph PROVIDERS[Choose wallet provider]
        P1[[🦊 MetaMask<br/>Most popular]]
        P2[[🔗 WalletConnect<br/>200+ wallets]]
        P3[[🌈 Coinbase Wallet]]
        P4[[💼 Ledger Hardware]]
        P1 --- P2
        P2 --- P3
        P3 --- P4
    end

    subgraph CONNECTED[Already connected]
        CW[Wallet: 0x1234...5678]
        CT[[Disconnect]]
        NET[Network: Polygon Mainnet]
        CW --- CT
        CT --- NET
    end

    subgraph SBTS[Your Soulbound Tokens]
        SBT1[🎓 Public Speaking Passport]
        SBT2[📊 Block: 51234567]
        SBT3[[View on PolygonScan →]]
        SBT1 --- SBT2
        SBT2 --- SBT3
    end

    subgraph NEXT[Next steps]
        N1[1. Connect wallet above]
        N2[2. Earn a Skill Passport]
        N3[3. Auto-mint SBT on-chain]
        N4[4. Share with employers]
        N1 --- N2
        N2 --- N3
        N3 --- N4
    end

    HEADER ==> HERO
    HERO --> PROVIDERS
    PROVIDERS --> CONNECTED
    CONNECTED --> SBTS
    SBTS --> NEXT

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    class P1,P2 primary
```

**Networks supported:** Polygon, Base, Optimism (default = Polygon).
**Soulbound:** SBT không thể transfer, chỉ revoke bởi admin/user.
