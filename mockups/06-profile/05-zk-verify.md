# zk-SNARK Verification

> **Mục đích:** Privacy-preserving verification cho HR tech (reveal rating mà không show identity).

```mermaid
flowchart TB
    subgraph HEADER[Header]
        H1[← Passport]
        H2[Privacy Verification]
        H1 --- H2
    end

    subgraph INTRO[Intro]
        I1[Generate zero-knowledge proof]
        I2[Verify your Skill Passport without revealing identity]
        I3[Use case: Share with employer anonymously]
        I1 --- I2
        I2 --- I3
    end

    subgraph CLAIM[What to prove?]
        C1[[✓ Rating ≥ 4.5 stars]
        C2[[✓ Sessions completed ≥ 10]
        C3[[✓ Verified identity]
        C4[[✓ Skill: Public Speaking]
        C5[[✗ Don't reveal: name photo email country]
        C1 --- C2
        C2 --- C3
        C3 --- C4
        C4 --- C5
    end

    subgraph GEN[Generate Proof]
        G1[Generating zk-SNARK proof...]
        G2[Time: 3.2 seconds]
        G3[Circuit: rating-threshold-v1]
        G1 --- G2
        G2 --- G3
        BAR[████████░░ 80%]
    end

    subgraph RESULT[Proof Generated]
        R1[┌──────────────────────────────────┐]
        R2[│ ✅ Proof valid                   │]
        R3[│ 📊 Claim: rating ≥ 4.5          │]
        R4[│ 🔒 Hidden: name photo email     │]
        R5[│ 🆔 Proof ID: 0xabc...def        │]
        R6[│ 🪶 Size: 256 bytes                │]
        R7[│ ⛓️ Verify on Polygon zkEVM        │]
        R8[│                                  │]
        R9[│ [📋 Copy proof] [🔗 Share link]  │]
        R10[└──────────────────────────────────┘]
        R1 --> R2
        R2 --> R3
        R3 --> R4
        R4 --> R5
        R5 --> R6
        R6 --> R7
        R7 --- R8
        R8 --- R9
        R9 --- R10
    end

    subgraph VERIFY[Verify by HR]
        VR1[[Verify at verify.skillseed.com]]
        VR2[HR upload proof → see TRUE/FALSE]
        VR1 --- VR2
    end

    HEADER ==> INTRO
    INTRO --> CLAIM
    CLAIM --> GEN
    GEN --> RESULT
    RESULT --> VERIFY

    classDef primary fill:#10B981,stroke:#047857,color:#fff
    classDef privacy fill:#6366F1,stroke:#4F46E5,color:#fff
    class VR1 primary
    class C5,R4 privacy
```

**Tech:** Circom 2 circuit + snarkjs prover + Polygon zkEVM verifier.
**Privacy:** Selective disclosure — chỉ reveal claim cần thiết.
