# J2N Motion Transition

A set of reusable React components for easy animations using `framer-motion`. These components are designed to handle common transition effects like scrolling, fading, and scaling with simple props.

## Installation

Ensure you have `framer-motion` installed in your project:

```bash
yarn add framer-motion
# or
npm install framer-motion
```

## Components

### 1. J2NMotionScroll

Triggers a directional slide animation when the element enters the viewport.

**Usage:**

```tsx
import { J2NMotionScroll } from "@repo/ui/components/atoms/J2NMotionTransition";

<J2NMotionScroll direction="up" duration={0.5} delay={0.2}>
  <h1>I slide up when scrolled into view!</h1>
</J2NMotionScroll>;
```

**Props:**

| Prop        | Type                                            | Default               | Description                               |
| ----------- | ----------------------------------------------- | --------------------- | ----------------------------------------- |
| `children`  | `ReactNode`                                     | Required              | Content to animate.                       |
| `direction` | `'up' \| 'down' \| 'left' \| 'right' \| 'none'` | `'up'`                | Direction of the slide.                   |
| `once`      | `boolean`                                       | `true`                | If true, animation plays only once.       |
| `delay`     | `number`                                        | `0`                   | Delay in seconds before animation starts. |
| `duration`  | `number`                                        | `0.45`                | Duration of the animation in seconds.     |
| `margin`    | `string`                                        | `'0px 0px -50px 0px'` | Margin to trigger `inView`.               |
| `className` | `string`                                        | `undefined`           | Additional class names.                   |

---

### 2. J2NMotionFade

Triggers a simple opacity fade-in animation when the element enters the viewport.

**Usage:**

```tsx
import { J2NMotionFade } from "@repo/ui/components/atoms/J2NMotionTransition";

<J2NMotionFade delay={0.3}>
  <img src="image.jpg" alt="Fade in" />
</J2NMotionFade>;
```

**Props:**

| Prop        | Type        | Default               | Description                         |
| ----------- | ----------- | --------------------- | ----------------------------------- |
| `children`  | `ReactNode` | Required              | Content to animate.                 |
| `once`      | `boolean`   | `true`                | If true, animation plays only once. |
| `delay`     | `number`    | `0`                   | Delay in seconds.                   |
| `duration`  | `number`    | `0.5`                 | Duration in seconds.                |
| `margin`    | `string`    | `'0px 0px -50px 0px'` | Margin to trigger `inView`.         |
| `className` | `string`    | `undefined`           | Additional class names.             |

---

### 3. J2NMotionScale

Triggers a scaling (zoom-in) animation when the element enters the viewport.

**Usage:**

```tsx
import { J2NMotionScale } from "@repo/ui/components/atoms/J2NMotionTransition";

<J2NMotionScale initialScale={0.8} duration={0.4}>
  <button>Click Me</button>
</J2NMotionScale>;
```

**Props:**

| Prop           | Type        | Default               | Description                         |
| -------------- | ----------- | --------------------- | ----------------------------------- |
| `children`     | `ReactNode` | Required              | Content to animate.                 |
| `initialScale` | `number`    | `0.9`                 | The starting scale value (0 to 1).  |
| `once`         | `boolean`   | `true`                | If true, animation plays only once. |
| `delay`        | `number`    | `0`                   | Delay in seconds.                   |
| `duration`     | `number`    | `0.4`                 | Duration in seconds.                |
| `margin`       | `string`    | `'0px 0px -50px 0px'` | Margin to trigger `inView`.         |
| `className`    | `string`    | `undefined`           | Additional class names.             |

## Types

You can import types if needed:

```tsx
import {
  IMotionScrollProps,
  IMotionFadeProps,
  IMotionScaleProps,
} from "@repo/ui/components/atoms/J2NMotionTransition/J2NMotionTrasition.type";
```
