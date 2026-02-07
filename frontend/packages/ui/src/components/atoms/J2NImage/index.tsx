"use client";

import Image from "next/image";
import styled from "styled-components";

const Frame = styled.div`
  position: relative;
  padding: 10px;

  /* Top-left corner L-shaped border (horizontal part) */
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 300px;
    height: 3px;
    background: #75616a;
  }

  /* Top-left corner L-shaped border (vertical part) */
  &::after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 3px;
    height: 300px;
    background: #75616a;
  }

  /* Bottom-right corner L-shaped border (horizontal part) */
  .corner-br-horizontal {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 300px;
    height: 3px;
    background: #75616a;
  }

  /* Bottom-right corner L-shaped border (vertical part) */
  .corner-br-vertical {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 3px;
    height: 300px;
    background: #75616a;
  }

  /* Clip shape - cut top-right and bottom-left corners */
  .clip-shape {
    position: relative;
    clip-path: polygon(
      0 0,
      calc(100% - 40px) 0,
      100% 40px,
      100% 100%,
      40px 100%,
      0 calc(100% - 40px)
    );
    overflow: hidden;
    background: #75616a;
  }
`;

interface ImageFrameProps {
  src: string;
  alt: string;
  classNames?: string;
  width?: string | number;
  height?: string | number;
  sizes?: string;
}

function J2NImage({
  src,
  alt,
  classNames,
  width = "100%",
  height = "100%",
  sizes,
}: ImageFrameProps) {
  const getIntrinsicValue = (val: string | number) => {
    if (typeof val === "number") return val;
    if (typeof val === "string" && val.endsWith("px")) {
      const num = parseFloat(val);
      return isNaN(num) ? undefined : num;
    }
    return undefined;
  };

  const intrinsicWidth = getIntrinsicValue(width);
  const intrinsicHeight = getIntrinsicValue(height);
  const useFill = intrinsicWidth === undefined || intrinsicHeight === undefined;

  return (
    <Frame
      className={`image-wrapper h-auto w-fit ${classNames || ""} `}
      style={{ width: width, height: height }}
    >
      <div className="corner-br-horizontal" />
      <div className="corner-br-vertical" />
      <div
        className="clip-shape"
        style={{ width: "100%", height: "100%", position: "relative" }}
      >
        <Image
          className="image-item"
          src={src}
          alt={alt}
          width={!useFill ? intrinsicWidth : undefined}
          height={!useFill ? intrinsicHeight : undefined}
          fill={useFill}
          style={{ objectFit: "cover", objectPosition: "center" }}
          loading="lazy"
          sizes={sizes}
        />
      </div>
    </Frame>
  );
}

export default J2NImage;
