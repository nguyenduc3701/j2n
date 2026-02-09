"use client";

import { useEffect, useState } from "react";
import { Carousel, CarouselSlide } from "@mantine/carousel";
import Image from "next/image";

interface AutoCarouselProps {
  photos: {
    src: string;
    alt: string;
    width: number;
    height: number;
    className?: string;
  }[];
}

const AutoCarousel = ({ photos }: AutoCarouselProps) => {
  const [embla, setEmbla] = useState<any>(null);

  useEffect(() => {
    if (embla) {
      const interval = setInterval(() => {
        embla.scrollNext();
      }, 5000);

      return () => clearInterval(interval);
    }
  }, [embla]);

  return (
    <Carousel
      withIndicators
      height={800}
      slideSize="50%"
      slideGap={15}
      emblaOptions={{ loop: true, align: "start" }}
      getEmblaApi={setEmbla}
    >
      {photos.map((photo, index) => (
        <CarouselSlide key={index}>
          <Image
            src={photo.src}
            alt={photo.alt}
            width={photo.width}
            height={photo.height}
            className="w-full h-full object-cover shadow-lg rounded-md"
          />
        </CarouselSlide>
      ))}
    </Carousel>
  );
};

export default AutoCarousel;
