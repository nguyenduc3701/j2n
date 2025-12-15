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
    width: 100px;
    height: 3px;
    background: #e3e1e1;
  }

  /* Top-left corner L-shaped border (vertical part) */
  &::after {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 3px;
    height: 100px;
    background: #e3e1e1;
  }

  /* Bottom-right corner L-shaped border (horizontal part) */
  .corner-br-horizontal {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 100px;
    height: 3px;
    background: #e3e1e1;
  }

  /* Bottom-right corner L-shaped border (vertical part) */
  .corner-br-vertical {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 3px;
    height: 100px;
    background: #e3e1e1;
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
    background: #e3e1e1;
  }
`;

interface ImageFrameProps {
  src: string;
  alt: string;
  classNames?: string;
  width?: string;
  height?: string;
}

function J2NImage({ src, alt, classNames, width, height }: ImageFrameProps) {
  const imageWidth = Number(width?.replace("px", "")) || 300;
  const imageHeight = Number(height?.replace("px", "")) || 300;
  return (
    <Frame
      className={`image-wrapper h-auto w-fit ${classNames || ""} `}
      style={{ width: imageWidth }}
    >
      <div className="corner-br-horizontal" />
      <div className="corner-br-vertical" />
      <div className="clip-shape">
        <Image
          className="image-item"
          src={src}
          alt={alt}
          width={imageWidth}
          height={imageHeight}
          objectFit="cover"
          objectPosition="center"
          loading="lazy"
        />
      </div>
    </Frame>
  );
}

export default J2NImage;
