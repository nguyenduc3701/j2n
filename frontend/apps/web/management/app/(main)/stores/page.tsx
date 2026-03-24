"use client";

import React from "react";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";

const StoresPage = () => {
  return (
    <J2NMotionFade>
      <div className="store-wrapper px-25 pt-25">
        <TransTitle tKey="Stores" />
      </div>
    </J2NMotionFade>
  );
};

export default StoresPage;
