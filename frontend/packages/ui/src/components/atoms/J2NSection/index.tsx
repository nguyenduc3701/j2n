import React from "react";

interface SectionProps {
  children: React.ReactNode;
  name: string;
  className?: string;
}

const J2NSection: React.FC<SectionProps> = ({ children, name, className }) => {
  return (
    <section
      className={`section-wrapper w-full h-auto section-${name} ${className}`}
      id={`section-${name}`}
    >
      {children}
    </section>
  );
};

export default J2NSection;
