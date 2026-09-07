# SkillSeed — Wireframe UI Flow & User Research Script

> **Mục đích:** Wireframe ASCII cho tất cả màn hình chính + User Journey Maps + Script phỏng vấn validation cho 30 người. Document này giúp team Design + Product validate UX và test idea trước khi code.

> **Last updated:** 2026-09-06

---

## Mục lục

### Phần A — UI Design
1. [Design Principles & Brand](#1-design-principles--brand)
2. [Design System (Tokens)](#2-design-system-tokens)
3. [User Personas & Journey Maps](#3-user-personas--journey-maps)
4. [Screen-by-Screen Wireframes](#4-screen-by-screen-wireframes)
5. [Mobile App Information Architecture](#5-mobile-app-information-architecture)
6. [Critical UX Flows](#6-critical-ux-flows)
7. [Accessibility Guidelines](#7-accessibility-guidelines)

### Phần B — User Research
8. [Research Goals & Methodology](#8-research-goals--methodology)
9. [Recruiting Participants](#9-recruiting-participants)
10. [Interview Scripts (3 personas)](#10-interview-scripts-3-personas)
11. [Survey Templates](#11-survey-templates)
12. [Usability Testing Protocol](#12-usability-testing-protocol)
13. [Synthesis & Analysis Framework](#13-synthesis--analysis-framework)

---

## 1. Design Principles & Brand

### 1.1. Core Design Principles

```
┌─────────────────────────────────────────────────────┐
│  1. CLARITY FIRST                                   │
│     Mỗi screen phải trả lời 1 câu hỏi             │
│     User không cần suy nghĩ "bước tiếp là gì?"      │
│                                                      │
│  2. WARMTH THROUGH HUMAN CONNECTION                  │
│     Sản phẩm về con người, không phải algorithm     │
│     → Ảnh đại diện, tên, story nổi bật              │
│                                                      │
│  3. PROGRESSIVE TRUST                                │
│     Bắt đầu nhẹ nhàng (explore), tăng commit dần    │
│     → Không yêu cầu đăng ký ngay                   │
│                                                      │
│  4. MOBILE-FIRST ALWAYS                              │
│     80%+ users trên mobile                          │
│     → Touch targets ≥ 44px                          │
│                                                      │
│  5. INCLUSIVE BY DEFAULT                             │
│     Đa ngôn ngữ, đa văn hóa                        │
│     → Icons + text, không text-only                  │
└─────────────────────────────────────────────────────┘
```

### 1.2. Brand Personality

- **Tone**: Friendly, encouraging, non-judgmental
- **Voice**: "We" (community), "You" (user) — không "We" corporate
- **Tagline variations**:
  - "Teach what you know. Learn what you love."
  - "Your skills have value."
  - "Time is the only currency that matters."

### 1.3. Visual Mood

| Mood | Implementation |
|------|----------------|
| Warm | Rounded corners (8-16px), soft shadows, warm pastels |
| Trustworthy | Real photos, verified badges, transparent info |
| Energetic | Bright primary color, dynamic micro-interactions |
| Modern | Sans-serif, generous whitespace, clean icons |

---

## 2. Design System (Tokens)

### 2.1. Color Palette

```
┌─────────────────────────────────────────────────────┐
│  PRIMARY                                             │
│  ▓▓ #10B981  Emerald-500    Main brand, CTAs        │
│  ▓▓ #059669  Emerald-600    Hover/active states     │
│  ▓▓ #047857  Emerald-700    Pressed states          │
│                                                      │
│  SECONDARY (Accent)                                  │
│  ░░ #F59E0B  Amber-500      Seeds, rewards, badges  │
│  ░░ #D97706  Amber-600      Hover                   │
│                                                      │
│  NEUTRAL                                             │
│  ██ #FFFFFF  White          Cards, surfaces         │
│  ░░ #F9FAFB  Gray-50        Backgrounds             │
│  ░░ #E5E7EB  Gray-200       Borders, dividers       │
│  ░░ #6B7280  Gray-500       Secondary text          │
│  ██ #111827  Gray-900       Primary text            │
│                                                      │
│  SEMANTIC                                            │
│  🟢 #10B981  Success        "Session completed"     │
│  🟡 #F59E0B  Warning        "Skill seed expiring"   │
│  🔴 #EF4444  Error          "Booking failed"        │
│  🔵 #3B82F6  Info           "New match available"   │
│                                                      │
│  SKILL TIER COLORS                                   │
│  🌱 #84CC16  Sprout (new)                            │
│  🌿 #22C55E  Sapling (5+ sessions)                   │
│  🌳 #16A34A  Tree (20+ sessions, verified)           │
│  🌲 #15803D  Forest (100+ sessions)                  │
│  🏆 #CA8A04  Legend (500+ sessions)                  │
└─────────────────────────────────────────────────────┘
```

### 2.2. Typography

| Use | Font | Size | Weight | Line Height |
|-----|------|------|--------|-------------|
| H1 | Inter | 32px | 700 | 1.2 |
| H2 | Inter | 24px | 600 | 1.3 |
| H3 | Inter | 20px | 600 | 1.4 |
| Body L | Inter | 18px | 400 | 1.5 |
| Body | Inter | 16px | 400 | 1.5 |
| Body S | Inter | 14px | 400 | 1.4 |
| Caption | Inter | 12px | 500 | 1.3 |
| Button | Inter | 16px | 600 | 1.0 |

### 2.3. Spacing System

```
4px → 8px → 12px → 16px → 20px → 24px → 32px → 40px → 48px → 64px

Sử dụng Tailwind scale: p-2, p-4, p-6, p-8...
```

### 2.4. Components Library

| Component | Spec |
|-----------|------|
| Button (Primary) | bg #10B981, text white, h-48px, rounded-12px, font-semibold |
| Button (Secondary) | bg white, border #10B981, text #10B981 |
| Card | bg white, shadow-sm, rounded-16px, p-16px |
| Input | border #E5E7EB, focus border #10B981, h-48px, rounded-8px |
| Avatar | circular, 32/48/64/96px sizes |
| Badge | rounded-full, px-12px, py-4px, font-12px |
| Modal | overlay bg-black/50, content rounded-16px |
| Toast | top-right, auto-dismiss 3s |

---

## 3. User Personas & Journey Maps

### 3.1. Persona 1: "Linh" — Sinh viên IT (18-24)

```
┌──────────────────────────────────────────────────┐
│  LINH, 21 tuổi                                   │
│  Sinh viên năm 3, ĐH Bách Khoa HCM              │
│                                                      │
│  Goals:                                             │
│  • Học React + TypeScript để đi làm freelance    │
│  • Cải thiện tiếng Anh để apply job remote        │
│  • Kiếm thêm thu nhập từ dạy Java                │
│                                                      │
│  Frustrations:                                      │
│  • Khóa học online quá đắt ($200-500)            │
│  • Mentor chất lượng khan hiếm                    │
│  • Sợ nói tiếng Anh với người nước ngoài         │
│                                                      │
│  Devices: iPhone 13, MacBook Air M1               │
│  Internet: Wifi 50Mbps                            │
│  Tech savviness: Cao                               │
└──────────────────────────────────────────────────┘
```

**Journey Map:**
```
Awareness        Consideration      Onboarding       First Match
    │                  │                  │                  │
    ▼                  ▼                  ▼                  ▼
"Thấy ad FB      "Vào landing       "Onboarding       "Match với
 từ bạn"         page, xem         7 bước, 3        Mai - React
                  video demo"        phút"            mentor"
    │                  │                  │                  │
    ▼                  ▼                  ▼                  ▼
Pain: Không       Pain: Quá nhiều    Pain: Phải nhập   Joy: "Wow AI
biết có free     app EdTech,        quá nhiều         matched hoàn
alternative      không tin          thông tin         hảo, Mai có
                                    cá nhân          lịch trùng mình"
```

### 3.2. Persona 2: "Mai" — Senior Developer (25-35)

```
┌──────────────────────────────────────────────────┐
│  MAI, 29 tuổi                                     │
│  Senior Java Developer tại VinAI                │
│                                                      │
│  Goals:                                             │
│  • Chia sẻ kiến thức, mentor cho juniors         │
│  • Học UX Design & Product Management (career     │
│    transition)                                     │
│  • Network với dev quốc tế                       │
│                                                      │
│  Frustrations:                                      │
│  • LinkedIn Premium quá đắt cho mentor 1-1       │
│  • MentorCruise chỉ có tech, không có design     │
│  • Ban ngày bận, muốn học buổi tối             │
│                                                      │
│  Devices: iPhone 14, MacBook Pro                  │
│  Schedule: Busiest 9am-6pm, free 7-9pm           │
│  Willingness to pay: Cao                          │
└──────────────────────────────────────────────────┘
```

### 3.3. Persona 3: "Bà Lan" — Nghỉ hưu (55+)

```
┌──────────────────────────────────────────────────┐
│  BÀ LAN, 62 tuổi                                  │
│  Giáo viên tiếng Anh về hưu                     │
│                                                      │
│  Goals:                                             │
│  • Chia sẻ 35 năm kinh nghiệm dạy tiếng Anh     │
│  • Chống cô đơn, kết nối với người trẻ         │
│  • Học yoga + nấu ăn mới (hobby)                │
│                                                      │
│  Frustrations:                                      │
│  • Cô đơn sau khi nghỉ hưu                      │
│  • Không biết cách dùng Zoom, app phức tạp      │
│  • Con cháu bận, ít nói chuyện                  │
│                                                      │
│  Devices: iPad (con tặng), Samsung Galaxy cũ    │
│  Schedule: Rảnh ngày, ít rảnh tối               │
│  Tech savviness: Thấp                             │
└──────────────────────────────────────────────────┘
```

---

## 4. Screen-by-Screen Wireframes

### 4.1. Onboarding Flow (7 bước, 3 phút)

#### Step 1: Welcome

```
┌─────────────────────────────────┐
│  [Logo SkillSeed]               │
│                                  │
│  Welcome to SkillSeed            │
│                                  │
│  Teach what you know.           │
│  Learn what you love.           │
│                                  │
│  [Illustration: 2 người         │
│   dạy nhau qua video call]       │
│                                  │
│  ─────────────────────          │
│  ● ○ ○ ○ ○ ○ ○                  │
│                                  │
│  [Get Started →]                 │
│                                  │
│  Already have account? Sign in   │
└─────────────────────────────────┘
```

**Notes:**
- Không yêu cầu đăng ký ngay
- Single CTA "Get Started"
- Illustration thay vì stock photo

#### Step 2: Account

```
┌─────────────────────────────────┐
│  ← Create your account          │
│                                  │
│  📧 Email                        │
│  [____________________]          │
│                                  │
│  🔒 Password                     │
│  [____________________]          │
│  At least 8 characters          │
│                                  │
│  📱 Phone (optional)             │
│  [+84 ___ ___ ___]              │
│  For verification & booking     │
│                                  │
│  ☐ I agree to Terms & Privacy   │
│                                  │
│  ─────────────────────          │
│  ● ● ○ ○ ○ ○ ○                  │
│                                  │
│  [Continue →]                    │
│                                  │
└─────────────────────────────────┘
```

**Notes:**
- Phone optional ở bước này (giảm friction)
- Password strength indicator
- Social login option ở footer (Google, Apple)

#### Step 3: Skill DNA - Bạn có thể dạy gì?

```
┌─────────────────────────────────┐
│  ← What can you teach?          │
│                                  │
│  We use this to match you       │
│  with people who want to learn. │
│                                  │
│  🔍 Search skills...            │
│  [____________________]         │
│                                  │
│  Popular:                        │
│  [Java] [Python] [Excel]        │
│  [Cooking] [Photography]        │
│  [English] [Public Speaking]    │
│                                  │
│  Your skills:                    │
│  ✓ Java Programming      [×]    │
│  ✓ Excel Advanced        [×]    │
│  ✓ Cooking               [×]    │
│  [+ Add another]                 │
│                                  │
│  ─────────────────────          │
│  ● ● ● ○ ○ ○ ○                  │
│                                  │
│  [Continue →]                    │
└─────────────────────────────────┘
```

#### Step 4: Skill DNA - Bạn muốn học gì?

```
┌─────────────────────────────────┐
│  ← What do you want to learn?    │
│                                  │
│  React? Yoga? Mandarin?          │
│  Add up to 5 skills.            │
│                                  │
│  🔍 Search skills...            │
│  [____________________]         │
│                                  │
│  Selected:                       │
│  ✓ React.js           [×]       │
│  ✓ UI Design          [×]       │
│  ✓ Public Speaking    [×]       │
│  [+ Add another]                 │
│                                  │
│  For each skill, how important? │
│  ┌──────────────────────────┐   │
│  │ React.js                  │   │
│  │ ○ ○ ○ ● ○  High          │   │
│  └──────────────────────────┘   │
│                                  │
│  ─────────────────────          │
│  ● ● ● ● ○ ○ ○                  │
│                                  │
│  [Continue →]                    │
└─────────────────────────────────┘
```

#### Step 5: Verification - Selfie

```
┌─────────────────────────────────┐
│  ← Verify it's you               │
│                                  │
│  Quick selfie for trust &       │
│  safety. We never share this.   │
│                                  │
│  [Camera preview - oval frame]  │
│                                  │
│       ┌─────────────┐           │
│       │             │           │
│       │   📷        │           │
│       │             │           │
│       └─────────────┘           │
│                                  │
│  [Take photo]                    │
│                                  │
│  Why we ask:                     │
│  • Build trust in community     │
│  • Prevent fake accounts        │
│  • Match with real people       │
│                                  │
│  ─────────────────────          │
│  ● ● ● ● ● ○ ○                  │
│                                  │
│  [Continue →]                    │
└─────────────────────────────────┘
```

#### Step 6: Preferences

```
┌─────────────────────────────────┐
│  ← A few quick questions         │
│                                  │
│  Where are you based?           │
│  [🇻🇳 Vietnam        ▼]         │
│                                  │
│  Time zone                       │
│  [Asia/Ho_Chi_Minh  ▼]          │
│                                  │
│  Languages you speak            │
│  ✓ Vietnamese                   │
│  ✓ English                      │
│  [+ Add]                        │
│                                  │
│  How do you learn best?         │
│  ● Hands-on projects            │
│  ○ Theory first                 │
│  ○ Discussion                   │
│  ○ Video examples               │
│                                  │
│  ─────────────────────          │
│  ● ● ● ● ● ● ○                  │
│                                  │
│  [Continue →]                    │
└─────────────────────────────────┘
```

#### Step 7: Schedule + Done

```
┌─────────────────────────────────┐
│  ← When are you free?           │
│                                  │
│  Tap time slots you're free    │
│  to teach or learn.             │
│                                  │
│        M   T   W   T   F   S S │
│  7am   □   □   □   □   □   □ □│
│  8am   ■   □   □   □   □   □ □│
│  9am   ■   □   □   □   □   □ □│
│ 10am   ■   □   ■   □   □   □ □│
│  ...                             │
│  7pm   □   ■   □   ■   □   □ □│
│  8pm   □   ■   □   ■   □   □ □│
│  9pm   □   ■   □   ■   □   □ □│
│                                  │
│  ■ = Available                   │
│                                  │
│  ─────────────────────          │
│  ● ● ● ● ● ● ●                  │
│                                  │
│  [Find my matches →]            │
└─────────────────────────────────┘

After tapping "Find my matches":

┌─────────────────────────────────┐
│                                  │
│        🎉 You're all set!        │
│                                  │
│  We found 12 people you could   │
│  teach and 8 who can teach you. │
│                                  │
│  [Illustration: connected people]│
│                                  │
│  Your free starter pack:        │
│  🌱 30 Skill Seeds               │
│                                  │
│  [See your matches →]            │
│                                  │
└─────────────────────────────────┘
```

### 4.2. Discover (Match Feed)

#### Mobile

```
┌─────────────────────────────────┐
│  Discover              🔍  ⚙️  │
│                                  │
│  Skills you want to learn:      │
│  [React ▼]  [Public Speaking]   │
│                                  │
│  ┌──────────────────────────┐   │
│  │ [Avatar] Mai Tran         │   │
│  │ ⭐ 4.8 (32) | 50 sessions│   │
│  │ 🇻🇳 Vietnam               │   │
│  │                           │   │
│  │ Teaches: React.js (L5)    │   │
│  │ 5 years experience        │   │
│  │ 60 seeds/hour             │   │
│  │                           │   │
│  │ 🤖 Why we matched:        │   │
│  │ "You both prefer hands-on │   │
│  │ projects, schedule overlaps│   │
│  │ Tuesday evenings."        │   │
│  │                           │   │
│  │ Next slot: Tue 7:00 PM    │   │
│  │                           │   │
│  │ [View profile] [Book →]   │   │
│  └──────────────────────────┘   │
│                                  │
│  ┌──────────────────────────┐   │
│  │ [Avatar] John Lee         │   │
│  │ ⭐ 4.9 (89) | 120 sessions│  │
│  │ 🇸🇬 Singapore              │   │
│  │ ...                       │   │
│  └──────────────────────────┘   │
│                                  │
│  [Load more ↓]                   │
│                                  │
│  [🏠 Home] [📚 Learn] [💬][👤] │
└─────────────────────────────────┘
```

#### Web

```
┌─────────────────────────────────────────────────────────────┐
│  SkillSeed  [Discover] [My Bookings] [Wallet] [👤 Linh]    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Filters:                                                    │
│  Skill: [React ▼]  Rating: [4+ ▼]  Time: [Evening ▼]      │
│  Language: [English ▼]  Verified: [✓]                       │
│                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌──────────────┐│
│  │ [Avatar]       │  │ [Avatar]       │  │ [Avatar]     ││
│  │ Mai Tran       │  │ John Lee       │  │ Linh Pham    ││
│  │ ⭐ 4.8 (32)    │  │ ⭐ 4.9 (89)    │  │ ⭐ 4.7 (15)  ││
│  │                │  │                │  │              ││
│  │ Teaches:       │  │ Teaches:       │  │ Teaches:     ││
│  │ React.js (L5)  │  │ System Design  │  │ UX Design    ││
│  │                │  │                │  │              ││
│  │ 🤖 Match 92%   │  │ 🤖 Match 88%   │  │ 🤖 Match 85% ││
│  │                │  │                │  │              ││
│  │ Next: Tue 7pm  │  │ Next: Wed 8pm  │  │ Next: Thu 6pm││
│  │                │  │                │  │              ││
│  │ [Book →]       │  │ [Book →]       │  │ [Book →]     ││
│  └────────────────┘  └────────────────┘  └──────────────┘│
│                                                              │
│  [Load more ↓]                                              │
└─────────────────────────────────────────────────────────────┘
```

### 4.3. Teacher Profile

```
┌─────────────────────────────────┐
│  ← [Avatar large]                │
│  Mai Tran                       │
│  ⭐ 4.8 (32 ratings)            │
│  📍 Ho Chi Minh City, VN        │
│  🌳 Tree tier (verified)        │
│                                  │
│  About me                       │
│  ─────────────────              │
│  "Senior dev at VinAI, love    │
│  teaching React. Patient with  │
│  beginners. Focus on real      │
│  projects, not theory."        │
│                                  │
│  Skills I teach                 │
│  ─────────────────              │
│  ✓ React.js     Level 5 (Pro)  │
│    5 years | 50 sessions taught│
│    [View past sessions]        │
│                                  │
│  ✓ JavaScript   Level 5         │
│    8 years | 30 sessions       │
│                                  │
│  My availability                │
│  ─────────────────              │
│  Mon-Fri: 7-9 PM (VN time)    │
│  Sat: 9 AM - 12 PM              │
│                                  │
│  Reviews (32)        [See all] │
│  ─────────────────              │
│  ⭐⭐⭐⭐⭐ Linh N. (1 week ago)│
│  "Excellent teacher! Helped me │
│  build my first React app..."  │
│  [Read more]                   │
│                                  │
│  Skill Passport                 │
│  ─────────────────              │
│  ✓ React.js (verified)         │
│  ✓ JavaScript (verified)       │
│  [View on LinkedIn →]          │
│                                  │
│  [📅 Book a session]            │
└─────────────────────────────────┘
```

### 4.4. Booking Flow

#### Step 1: Choose slot

```
┌─────────────────────────────────┐
│  ← Book with Mai                │
│                                  │
│  What do you want to learn?    │
│  ● React.js                     │
│  ○ JavaScript                   │
│                                  │
│  Choose duration:              │
│  [15min] [30min] [45min] [60min]│
│           ●                     │
│                                  │
│  Available slots (VN time):    │
│  Tue, Sep 10                    │
│  ● 7:00 PM - 7:30 PM            │
│  ○ 8:00 PM - 8:30 PM            │
│  Wed, Sep 11                    │
│  ● 7:00 PM - 7:30 PM            │
│  ...                             │
│                                  │
│  Cost: 30 Skill Seeds           │
│  Your balance: 60 Seeds         │
│  After: 30 Seeds                │
│                                  │
│  [Continue →]                   │
└─────────────────────────────────┘
```

#### Step 2: Confirm

```
┌─────────────────────────────────┐
│  ← Confirm booking              │
│                                  │
│  📋 Summary                     │
│  Teacher: Mai Tran              │
│  Skill: React.js                │
│  When: Tue Sep 10, 7:00 PM     │
│  Duration: 30 minutes          │
│  Cost: 30 Skill Seeds          │
│                                  │
│  💬 Notes for Mai (optional)    │
│  [____________________]         │
│  [____________________]         │
│                                  │
│  ⚠️ Cancellation policy:        │
│  Free cancel up to 12h before. │
│  After that, 50% Seeds refund. │
│                                  │
│  [Confirm & Pay 30 Seeds →]    │
└─────────────────────────────────┘
```

#### Step 3: Success

```
┌─────────────────────────────────┐
│  ✅ Booking confirmed!          │
│                                  │
│  📅 Tue Sep 10, 7:00 PM        │
│  with Mai Tran                  │
│                                  │
│  You'll get a reminder 1 hour │
│  before.                         │
│                                  │
│  [Add to Google Calendar]       │
│                                  │
│  [View booking details]         │
│  [Back to Discover]             │
└─────────────────────────────────┘
```

### 4.5. Video Session

```
┌─────────────────────────────────┐
│  [Video feed - Teacher]   [👤]│
│  ┌──────────────────────────┐  │
│  │                           │  │
│  │       MAI (large)        │  │
│  │                           │  │
│  │                           │  │
│  └──────────────────────────┘  │
│  [Self view - small]      📷   │
│                                  │
│  ⏱️ 24:35 / 30:00              │
│                                  │
│  [🎤] [📷] [🖥️ Share] [💬 Chat]│
│                                  │
│  [🤖 Co-pilot] [⚙️] [✕ Leave] │
│                                  │
│  ─────── AI Co-Pilot Panel ─── │
│  📝 Live notes:                 │
│  • Discussed useState vs       │
│    useReducer                   │
│  • Mai showed example project  │
│  • Action: Linh will build     │
│    a Todo app by next session  │
│                                  │
│  💡 Suggestion:                 │
│  "Want me to show a real        │
│  example of component           │
│  composition?"                  │
└─────────────────────────────────┘
```

**Notes:**
- AI Co-Pilot panel collapse được (mặc định thu gọn)
- Time warning khi còn 5 phút
- "Leave" button màu đỏ, confirm trước khi rời

### 4.6. Wallet

```
┌─────────────────────────────────┐
│  ← Wallet                       │
│                                  │
│  🌱 60 Seeds                    │
│  ─────                          │
│  = 60 minutes of teaching       │
│  = 2 hours of learning          │
│                                  │
│  Total earned: 240              │
│  Total spent: 180               │
│  Pending hold: 30               │
│                                  │
│  ⚠️ 30 Seeds expiring in 25 days│
│  [Use them before expiry →]     │
│                                  │
│  Recent transactions             │
│  ─────────────────              │
│  ✓ Earned 60 Seeds             │
│    Taught Linh JavaScript       │
│    2 days ago                   │
│                                  │
│  ✓ Spent 30 Seeds               │
│    Booked Mai - React.js        │
│    Sep 5                        │
│                                  │
│  ✓ Earned 30 Seeds             │
│    Taught John Public Speaking  │
│    Sep 1                        │
│                                  │
│  [See all transactions →]       │
│                                  │
│  ─────────────────              │
│  💎 Need more Seeds?            │
│  Buy a Seed Pack:              │
│  [100 seeds - $5]               │
│  [500 seeds - $20]              │
│  [1500 seeds - $50]             │
└─────────────────────────────────┘
```

### 4.7. Profile

```
┌─────────────────────────────────┐
│  [Avatar large]                 │
│  Linh Nguyen                    │
│  🌿 Sapling tier                │
│  Member since Aug 2026          │
│                                  │
│  📍 Vietnam                     │
│  🇻🇳 Vietnamese, 🇬🇧 English    │
│                                  │
│  [Edit profile]                 │
│                                  │
│  Stats                           │
│  ─────────────────              │
│  Sessions taught: 8             │
│  Sessions learned: 12           │
│  Avg rating: 4.7                │
│  Skill Seeds: 60                │
│                                  │
│  Skills I teach                 │
│  ─────────────────              │
│  ✓ Java Programming (L4)       │
│    3 years experience           │
│  ✓ Excel Advanced (L5)         │
│    7 years experience           │
│  ✓ Cooking (L3)                │
│  [+ Add skill]                  │
│                                  │
│  Skills I'm learning            │
│  ─────────────────              │
│  → React.js                    │
│  → UI Design                   │
│  → Public Speaking              │
│                                  │
│  My Skill Passports             │
│  ─────────────────              │
│  🏆 Java Programming            │
│    5 sessions | 4.8 rating      │
│    [View on LinkedIn →]        │
│                                  │
│  Verification                   │
│  ─────────────────              │
│  ✓ Email verified               │
│  ✓ Phone verified               │
│  ☐ Government ID (earn 2x more)│
│  ☐ Liveness check (get badges) │
│  [Verify now →]                 │
└─────────────────────────────────┘
```

### 4.8. Post-Session Rating

```
┌─────────────────────────────────┐
│  Session completed! 🎉          │
│                                  │
│  How was your session with Mai? │
│                                  │
│  Rate Mai (your teacher):       │
│  ─────────────────              │
│  Knowledge     ○ ○ ○ ○ ●        │
│  Clarity       ○ ○ ○ ● ○        │
│  Helpfulness   ○ ○ ○ ○ ●        │
│  Punctuality   ○ ○ ○ ○ ●        │
│  Friendliness  ○ ○ ○ ○ ●        │
│                                  │
│  💬 Write a review (optional)   │
│  [____________________]         │
│  [____________________]         │
│                                  │
│  ☐ I'd recommend Mai to others │
│                                  │
│  30 Skill Seeds will be         │
│  transferred to Mai.            │
│                                  │
│  [Submit & Earn Passport ✓]     │
│                                  │
└─────────────────────────────────┘
```

---

## 5. Mobile App Information Architecture

```
📱 SkillSeed App
├── 🏠 Home (default tab)
│   ├── Discover (match feed)
│   ├── Today's bookings
│   └── Quick actions (book, teach)
│
├── 📚 Learn (tab)
│   ├── Browse categories
│   ├── My learning goals
│   └── Upcoming learner sessions
│
├── 💬 Sessions (tab)
│   ├── Upcoming
│   ├── Past
│   └── Join active session
│
├── 👤 Profile (tab)
│   ├── My profile (public view)
│   ├── Edit profile
│   ├── My skills (taught/wanted)
│   ├── Availability
│   ├── Skill Passports
│   ├── Verification
│   └── Settings
│
└── 💰 Wallet (header button)
    ├── Balance
    ├── Transactions
    ├── Buy seeds
    └── Seed packs

Settings (sub-menu):
├── Notifications
├── Privacy
├── Language & region
├── Help & support
├── About
└── Sign out
```

---

## 6. Critical UX Flows

### 6.1. First-time User → First Booking (target: 7 phút)

```
1. Land on Welcome (0:00)
   ↓ [Get Started]
2. Create account (0:30)
   ↓ [Continue]
3. Add skill to teach (1:30)
   ↓ [Continue]
4. Add skill to learn (2:00)
   ↓ [Continue]
5. Selfie verify (2:30)
   ↓ [Continue]
6. Set location + languages (3:00)
   ↓ [Continue]
7. Set availability (3:30)
   ↓ [Find my matches]
8. See "You're all set" + first matches (4:00)
   ↓ [Tap match]
9. Teacher profile (4:30)
   ↓ [Book a session]
10. Choose slot (5:00)
    ↓ [Continue]
11. Confirm booking (5:30)
    ↓ [Confirm & Pay]
12. Success screen (6:00)
    ↓ [Add to calendar]
13. Back to Discover (6:30)
```

### 6.2. Returning User → Book again (target: 30 giây)

```
1. Open app
2. Tap 🔔 notification: "Mai has a new slot!"
3. Tap notification → goes to booking flow with pre-filled teacher
4. Tap [Book again with Mai]
5. Confirm (1-tap if previously confirmed method)
6. Done ✓
```

### 6.3. Earn Seeds after Teaching

```
1. Session ends → both see "Rate your session" screen
2. Both submit rating
3. Teacher's wallet shows: "+30 Seeds earned"
4. Notification: "You earned a Skill Passport milestone! 5 sessions taught"
5. Tap → Passport detail screen
6. [Share to LinkedIn]
```

---

## 7. Accessibility Guidelines

### 7.1. WCAG 2.1 AA Compliance

| Requirement | Implementation |
|-------------|----------------|
| Color contrast | All text ≥ 4.5:1 ratio (test với WebAIM) |
| Touch targets | Minimum 44×44px (Apple HIG) |
| Focus states | Visible outline on all interactive elements |
| Alt text | All images, icons có descriptive alt |
| Screen reader | ARIA labels cho buttons, status announcements |
| Keyboard nav | All flows navigable bằng keyboard |
| Captions | Video sessions có live captions |
| Font scaling | Support 100%-200% browser zoom |
| Reduced motion | Respect `prefers-reduced-motion` |

### 7.2. Inclusive Design

- **Language**: Default theo location, switch dễ dàng
- **Low literacy**: Icons + text, không text-only critical actions
- **Slow internet**: Skeleton screens, lazy load images
- **Older users (Bà Lan persona)**: Larger default font option, simpler onboarding
- **Color blind**: Không rely on color alone (e.g., error = icon + text + color)

### 7.3. Mobile-Specific

- Safe area (notch, home indicator)
- Haptic feedback cho important actions
- Offline state cho read-only screens
- Pull-to-refresh với animation
- Native gestures (swipe back where possible)

---

## 8. Research Goals & Methodology

### 8.1. Research Questions (cần trả lời)

```
┌─────────────────────────────────────────────────────┐
│  PRIMARY QUESTIONS                                   │
│  1. Có đủ người SẴN SÀNG dạy miễn phí không?      │
│  2. Có đủ người SẴN SÀNG học qua strangers không? │
│  3. Seed economy có khả thi không?                  │
│  4. AI matching có tạo giá trị thật không?         │
│  5. Willingness to pay cho Premium là bao nhiêu?   │
│                                                      │
│  SECONDARY QUESTIONS                                 │
│  6. Đối thủ nào user đang dùng?                    │
│  7. Pain points chính khi học online?              │
│  8. Onboarding có quá dài / quá ngắn?             │
│  9. Video call UX có accept được không?            │
│  10. Có concerns về trust/safety không?            │
└─────────────────────────────────────────────────────┘
```

### 8.2. Methodology

| Phase | Method | Participants | Goal |
|-------|--------|--------------|------|
| **Phase 0: Exploratory** | In-depth interviews | 15 (5 per persona) | Understand pain points, validate concept |
| **Phase 1: Concept test** | Survey + Figma prototype | 100+ | Quantify interest, willingness |
| **Phase 2: Usability** | Moderated usability test | 10 | Find UX issues |
| **Phase 3: Beta** | Live app test | 50 | Real behavior, retention |

### 8.3. Timeline (4 tuần)

| Week | Activity |
|------|----------|
| Week 1 | Recruit 15 interview participants, run 5 interviews |
| Week 2 | Run 10 more interviews, synthesize |
| Week 3 | Build Figma prototype, run survey 100+ responses |
| Week 4 | Run usability tests with 10 people |

---

## 9. Recruiting Participants

### 9.1. Recruitment Criteria

| Persona | Criteria | Where to find |
|---------|----------|---------------|
| **Linh** (Student) | 18-24, đang học ĐH, có 1-3 kỹ năng có thể dạy | Facebook groups sinh viên, LinkedIn |
| **Mai** (Pro) | 25-35, có việc làm, muốn học ngang/hobby | LinkedIn, Slack communities |
| **Bà Lan** (Senior) | 55+, đã nghỉ hưu hoặc sắp nghỉ, có expertise | Senior centers, Facebook groups người cao tuổi |

### 9.2. Recruitment channels

- **Warm network**: Hỏi bạn bè, mentors, advisors
- **Cold outreach**:
  - Facebook groups (500K+ members): "Học Java", "Marketing Digital"
  - Reddit: r/learnprogramming, r/languagelearning
  - LinkedIn: search "Software Engineer Vietnam" + filter by age
- **Paid**: $5-15/user qua Respondent.io, UserTesting.com
- **Email database**: Mua list từ data brokers (cẩn thận GDPR)

### 9.3. Screening Survey (5 câu, 2 phút)

```
1. Tuổi của bạn?
   ○ 18-24  ○ 25-35  ○ 36-50  ○ 51+

2. Bạn có đang học kỹ năng mới nào không?
   ○ Có, tích cực (3+ giờ/tuần)
   ○ Có, thỉnh thoảng
   ○ Không

3. Bạn có kỹ năng nào có thể dạy người khác?
   ○ Có, nhiều (chuyên môn cao)
   ○ Có, vài kỹ năng
   ○ Có thể, hobby
   ○ Không chắc

4. Trong 6 tháng qua, bạn đã trả tiền cho khóa học online chưa?
   ○ Có, >$200  ○ Có, <$200  ○ Không

5. Bạn sẵn sàng dạy miễn phí 1 giờ/tuần trên app?
   ○ Sẵn sàng  ○ Có thể  ○ Không
```

→ Invite những người trả lời "Sẵn sàng" hoặc "Có thể" + match persona.

### 9.4. Incentive

| Region | Incentive |
|--------|-----------|
| Vietnam | 200K VND (~8 USD) cash hoặc Starbucks voucher |
| Singapore | $15 SGD voucher |
| Global | $15 USD Amazon gift card |
| Senior (55+) | Thư cảm ơn + $20 voucher (cao hơn vì ít incentive) |

---

## 10. Interview Scripts (3 personas)

### 10.1. Universal opener (5 phút)

```
Researcher: "Cảm ơn bạn đã dành thời gian. Mình sẽ hỏi một số câu về
cách bạn học các kỹ năng mới. Không có câu trả lời đúng hay sai -
mình muốn nghe kinh nghiệm thật của bạn.

Mình sẽ ghi âm để không bỏ sót chi tiết, được không?
[Đợi đồng ý]

Ok, bắt đầu nhé!"
```

### 10.2. Persona 1: Linh (Student) — 30 phút

**Section A: Background (5 min)**

```
1. Bạn đang học trường nào, năm mấy?
2. Bạn học ngành gì? Tại sao chọn ngành đó?
3. Điểm mạnh học tập của bạn là gì?
4. Ngoài giờ học, bạn làm gì? (Part-time job, hobby, club?)
```

**Section B: Learning behavior (10 min)**

```
5. Trong 6 tháng qua, bạn đã học kỹ năng mới nào ngoài trường?
6. Bạn học bằng cách nào? (YouTube, khóa online, bạn bè, sách?)
7. Khi gặp khó khăn, bạn thường làm gì? (Google, hỏi bạn, post forum?)
8. Có khi nào bạn muốn học từ 1 người cụ thể không? Cảm giác thế nào?
9. Bạn đã bao giờ trả tiền cho ai đó dạy bạn 1-1 chưa?
   - Bao nhiêu? Cho cái gì?
   - Có đáng không? Tại sao?
```

**Section C: Concept validation (10 min)**

```
10. [Show wireframe Discover page]
    Bạn hiểu màn hình này muốn nói gì?
    Nếu có 1 app thế này, bạn có dùng không? Tại sao?

11. Giả sử bạn học React từ Mai - 1 dev ở VN - qua video call.
    Bạn sẽ trả bằng cách nào?
    a) Tiền ($30/giờ)
    b) Dạy lại Excel cho ai đó (free)
    c) Không muốn trả gì cả
    d) Khác: ___

12. Nếu app cho bạn 30 "Skill Seeds" miễn phí để bắt đầu,
    bạn có lo lắng gì không? (Privacy, chất lượng, scam?)

13. Điều gì sẽ khiến bạn NGẠI dùng app?
    a) Phải nói chuyện với strangers
    b) Video call với người lạ
    c) Cho điểm/rating người khác
    d) Khác: ___
```

**Section D: Willingness to teach (5 min)**

```
14. Bạn có kỹ năng nào đủ tốt để dạy người khác?
    (Java, Excel, tiếng Anh, nấu ăn,...)
15. Nếu có người muốn học Java từ bạn, bạn:
    a) Sẵn sàng dạy 1 giờ/tuần miễn phí
    b) Chỉ dạy nếu được gì đó
    c) Không có thời gian
    d) Không tự tin

16. Nếu bạn dạy và được nhận "Skill Seeds" mà dùng để học lại,
    bạn có động lực hơn không? Tại sao?
```

**Section E: Pricing (5 min)**

```
17. Nếu app có gói Premium $9.9/tháng (AI matching tốt hơn,
    học unlimited, không quảng cáo), bạn có mua không?
    a) Chắc chắn  b) Có thể  c) Không

18. Bao nhiêu/tháng là hợp lý với bạn?
    $0 / $5 / $10 / $20 / khác?
```

**Wrap-up (5 min)**

```
19. Có điều gì mình chưa hỏi mà bạn muốn chia sẻ?
20. Nếu app này có thật, bạn sẽ thử trong tuần đầu không?
```

### 10.3. Persona 2: Mai (Professional) — 30 phút

**Section A: Background (5 min)**

```
1. Bạn làm gì? Ở đâu?
2. Bạn ở công ty này bao lâu?
3. Ngoài công việc chính, bạn có muốn học gì khác?
   (Career switch, hobby, side hustle)
4. Kỹ năng nào bạn tự tin nhất? Tại sao?
```

**Section B: Current learning (8 min)**

```
5. Bạn đã bao giờ dùng mentor 1-1 chưa?
   - Ở đâu? Trả bao nhiêu? Hiệu quả?
6. Bạn dùng Udemy, Coursera, hay gì? Bao nhiêu/tháng?
7. Có khi nào bạn muốn học 1 kỹ năng mà không tìm được
   giáo viên phù hợp không?
8. Schedule học của bạn thường thế nào? (Sáng, tối, weekend?)
```

**Section C: Teaching experience (8 min)**

```
9. Bạn đã bao giờ dạy/kèm ai chưa?
   (Đồng nghiệp, bạn bè, em, mentees)
10. Cảm giác dạy người khác thế nào? Có thích không?
11. Nếu dạy miễn phí trên app, bạn lo lắng gì?
    a) Mất thời gian
    b) Học viên không nghiêm túc
    c) Chất lượng của mình không đủ tốt
    d) Khác
12. Nếu được thưởng "Skill Seeds" dùng để học lại,
    điều này có ý nghĩa gì với bạn?
```

**Section D: Concept test (7 min)**

```
13. [Show wireframe - discover + profile]
    Bạn thấy gì ở app này?
    Cái gì hấp dẫn? Cái gì đáng ngờ?

14. Nếu app này cho phép bạn dạy React cho người ở Singapore,
    nhận Skill Seeds, rồi dùng Seeds học UX Design từ ai đó
    ở Mỹ - bạn thấy thế nào?

15. Trong 1 tháng đầu, bạn dự kiến sẽ dạy bao nhiêu giờ?
    0 / 1 / 2 / 4 / >4 giờ?
```

**Section E: B2B interest (2 min)**

```
16. Công ty bạn có chương trình upskilling không?
17. Nếu có app này cho công ty dùng (sharing skills nội bộ),
    bạn nghĩ HR có mua không? Tại sao?
```

**Wrap-up (5 min)**

```
18. Có điều gì khác bạn muốn chia sẻ?
19. Willingness to pay cho Premium $9.9/tháng?
20. Nếu có thật, bạn sẽ tham gia không?
```

### 10.4. Persona 3: Bà Lan (Senior) — 30 phút

**Section A: Background (5 min)**

```
1. Bạn đã nghỉ hưu bao lâu rồi?
2. Trước khi nghỉ hưu, bạn làm gì?
3. Hiện tại ngày bạn làm gì? (Có hoạt động gì không?)
4. Bạn có dùng smartphone/tablet không? Hay máy tính?
   - Dùng cho việc gì? (Zalo, Facebook, xem phim,...)
```

**Section B: Connection & loneliness (8 min)**

```
5. Từ khi nghỉ hưu, bạn có cảm thấy cô đơn không?
6. Bạn nói chuyện với ai mỗi ngày?
7. Bạn có muốn kết nối với người trẻ hơn không?
8. Bạn có dạy kèm ai trước đây không? (Cháu, hàng xóm,...)
```

**Section C: Teaching willingness (8 min)**

```
9. Nếu có người muốn học tiếng Anh từ bạn,
   bạn sẵn sàng dạy họ qua video call không?
10. Bạn dùng video call chưa? (Zoom, Messenger video?)
    - Cảm giác thế nào? Khó hay dễ?
11. Nếu app dạy bạn dùng chỉ trong 2 bước,
    bạn có thử không?
12. Bạn lo lắng gì khi dạy qua video?
    a) Không rành công nghệ
    b) Sợ nói sai
    c) Sợ người lạ
    d) Khác
```

**Section D: Concept test (5 min)**

```
13. [Show wireframe rất đơn giản - 3 màn hình chính]
    Bạn hiểu app này muốn nói gì không?

14. Nếu có người trẻ hỏi bạn: "Bà ơi, dạy cháu nói 'How are you'
    cho tự nhiên đi" - bạn sẽ dạy họ thế nào qua video?

15. Nếu mỗi lần dạy 30 phút bạn được "cảm ơn" bằng
    cách ai đó dạy bạn nấu món mới - bạn thấy sao?
```

**Section E: Pricing (4 min)**

```
16. Bạn sẵn sàng trả bao nhiêu cho 1 app dạy/học kỹ năng?
    Miễn phí / 50K / 100K / 200K VND/tháng?
17. Bạn có dùng app nào trả phí không?
    (Có bao nhiêu app? Trả bao nhiêu?)
```

**Wrap-up (5 min)**

```
18. Bạn có muốn thử app khi có không?
19. Có điều gì mình chưa hỏi?
20. Cảm ơn bạn rất nhiều!
```

---

## 11. Survey Templates

### 11.1. Concept Test Survey (15 câu, 5 phút)

```
1. Tuổi: [dropdown 18-24, 25-35, 36-50, 51+]
2. Quốc gia: [dropdown]
3. Nghề nghiệp: [text]
4. Trong 6 tháng qua, bạn đã trả tiền cho khóa học online?
   [Yes/No/Skip]
5. Nếu có, bao nhiêu tổng cộng?
   [$0 / <$50 / $50-200 / $200-1000 / >$1000]
6. Bạn có kỹ năng nào có thể dạy người khác không?
   [Yes, many / Yes, some / Maybe / No]
7. Bạn có sẵn sàng dạy 30 phút/tuần MIỄN PHÍ không?
   [Definitely / Probably / Maybe / Probably not / No]
8. Bạn có sẵn sàng học từ strangers qua video call?
   [Definitely / Probably / Maybe / Probably not / No]
9. [Show 1-paragraph concept + screenshot]
   "SkillSeed is a platform where you teach skills you know
    to earn 'Skill Seeds', then spend them to learn from others.
    AI matches you with the best peers. All in-app video,
    ratings, verifiable reputation."
   Bạn quan tâm đến mức nào? (1-10)
10. Bạn sẽ tải app nếu có sẵn?
    [Definitely / Probably / Maybe / Probably not / No]
11. Cái gì hấp dẫn nhất?
    [Multiple choice: AI matching, Free, Video, Reputation, Community]
12. Cái gì đáng lo nhất?
    [Multiple choice: Quality, Safety, Time waste, Tech issues]
13. Willingness to pay $9.9/month cho Premium?
    [Yes / Maybe / No]
14. Bạn đang dùng app học nào? (Chọn tất cả)
    [Udemy / Coursera / Skillshare / YouTube / Tutor / Khác]
15. Email liên hệ khi ra mắt: [email]
```

### 11.2. Post-Session Survey (5 câu, 1 phút)

```
1. Session này có đáng giá thời gian của bạn không?
   [1-5 stars]

2. Bạn có học được gì hữu ích không?
   [Yes, a lot / Yes, some / A little / No]

3. Teacher/Learner của bạn có friendly không?
   [1-5 stars]

4. Bạn sẽ book lại với người này không?
   [Definitely / Probably / Maybe / No]

5. Vấn đề gì cần cải thiện? (optional)
   [Text]
```

---

## 12. Usability Testing Protocol

### 12.1. Test setup

```
Location: User's home hoặc coworking space (thoải mái)
Duration: 45-60 phút
Equipment:
  - Test device (iPhone or Android, fresh)
  - Recording software (Lookback, Hotjar)
  - Prototype (Figma interactive)
  
Participants: 5 per persona = 15 total
Incentive: $20 voucher
```

### 12.2. Test tasks (5 tasks)

```
Task 1: Onboarding
  "Hãy tạo tài khoản mới và hoàn thành onboarding"
  [Quan sát: có bỏ cuộc không? Stuck ở đâu?]
  Success: Hoàn thành trong <5 phút

Task 2: Browse matches
  "Tìm 1 giáo viên để học [skill X]"
  [Quan sát: filter dùng không? Có scroll không?]
  Success: Tìm được 1 profile phù hợp

Task 3: Book session
  "Book 1 buổi học 30 phút với giáo viên bạn chọn"
  [Quan sát: hiểu Seed economy không? Confirm dễ không?]
  Success: Booking confirmed

Task 4: Wallet
  "Xem bạn có bao nhiêu Skill Seeds và lịch sử"
  [Quan sát: hiểu Seeds là gì? Lo lắng về expiry?]
  Success: Hiển thị đúng balance

Task 5: Rate session (post-task, sau khi mô tả)
  "Bạn vừa học xong, hãy rate giáo viên"
  [Quan sát: đánh giá các tiêu chí dễ không?]
  Success: Submit rating
```

### 12.3. Observation template

```
Per task:
  - Time to complete: ___ min
  - Success: Y/N
  - Errors encountered: ___
  - Asked for help: Y/N (what?)
  - Frustration signals: ___
  - Delight signals: ___
  - Verbatim quote: ___

After all tasks:
  - Overall satisfaction (1-10): ___
  - Would use in real life (1-10): ___
  - Top 3 issues to fix: ___
  - Top 3 things loved: ___
```

### 12.4. Severity rating

| Severity | Definition | Action |
|----------|------------|--------|
| **Critical** | Blocker - user không thể hoàn thành task | Fix trước launch |
| **Major** | Khó khăn, frustration cao | Fix trong 2 tuần |
| **Minor** | Inconvenient, có workaround | Fix trong 1 tháng |
| **Cosmetic** | Polish | Nice to have |

---

## 13. Synthesis & Analysis Framework

### 13.1. Affinity Mapping

Sau khi thu thập data từ interviews + surveys:

```
Step 1: Extract observations
  → Mỗi observation = 1 sticky note

Step 2: Cluster related observations
  → Nhóm theo theme:
     - Pain points
     - Desires
     - Objections
     - Feature requests
     - Pricing sensitivity

Step 3: Identify patterns
  → ≥3 participants mention = pattern
  → ≥5 participants = strong pattern

Step 4: Generate insights
  → "Users want..." (based on patterns)
  → "Users fear..." (based on objections)

Step 5: Prioritize
  → Must-have (P0): Blocks launch
  → Should-have (P1): Critical for retention
  → Nice-to-have (P2): Future iteration
```

### 13.2. Decision Framework

| Signal | Action |
|--------|--------|
| >60% "Definitely would use" | GREEN - proceed to MVP |
| 40-60% interested | YELLOW - need more validation |
| <40% interested | RED - pivot idea |
| >30% mention same concern | MUST address in MVP |
| <5% mention X | Defer X to post-MVP |

### 13.3. Go/No-Go criteria for MVP

```
✅ GO if:
- 60%+ survey respondents "probably/definitely" want to use
- 5+ interviewees say "I'd teach for free"
- 3+ interviewees say "I'd pay $9.9 for Premium"
- Top concern is solvable in MVP
- At least 1 person asks "when can I sign up?"

❌ NO-GO if:
- <40% interested
- Top concern is fundamental (e.g., "I just don't trust strangers")
- Nobody wants to teach for free
- Existing solution adequate
- Cost to validate > cost to pivot
```

### 13.4. Continuous research (post-launch)

| Frequency | Method | Sample size |
|-----------|--------|-------------|
| Weekly | In-app survey (NPS) | All users |
| Monthly | Survey to 100 active users | 100 |
| Quarterly | 10 in-depth interviews | 10 |
| Bi-annually | Full market research | 500+ |

### 13.5. Tools

| Tool | Use case | Cost |
|------|----------|------|
| Typeform / Google Forms | Surveys | Free - $25/mo |
| Lookback / Hotjar | Usability recording | $0 - $99/mo |
| Notion | Synthesis workspace | Free - $10/mo |
| Dovetail | Research repository | $20/mo |
| UserTesting.com | Recruit + usability | $49/user |
| Respondent.io | Recruit | $5-15/user |

---

## 📚 Phụ lục: Quick Reference

### Design checklist

```
□ Mỗi screen có 1 CTA chính
□ Touch targets ≥ 44px
□ Color contrast ≥ 4.5:1
□ Alt text cho mọi image
□ Loading states cho async actions
□ Empty states với CTA
□ Error states với recovery action
□ Mobile-first design
□ Dark mode (optional)
□ Multi-language ready (i18n keys)
```

### Research checklist

```
□ Recruitment criteria rõ ràng
□ Screener survey (5 câu)
□ Informed consent form
□ Recording consent
□ Interview script per persona
□ Observation template
□ Incentive đã chuẩn bị
□ Synthesis workspace (Notion)
□ Decision framework rõ ràng
```

### File deliverables

```
Sau research, bạn có:
1. Research report PDF (insights + recommendations)
2. User personas validated
3. UX issues list (prioritized)
4. Go/No-Go decision document
5. Updated wireframes dựa trên feedback
6. Survey data spreadsheet
7. Interview recordings (anonymized)
```

---

> **Liên hệ với tài liệu khác:**
> - **Product spec**: `SKILLSEED.md` §5 (Product Features)
> - **Code skeleton**: `SKILLSEED_CODE_SKELETON.md`
> - **API design**: `SKILLSEED_API_AND_DB.md` §6 (User API)
> - **Pitch deck**: `SKILLSEED_PITCH_DECK.md` (traction slides)

> **Tác giả:** SkillSeed Team — Phiên bản 1.0 — 2026-09-06
