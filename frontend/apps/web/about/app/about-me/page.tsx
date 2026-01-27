import J2NTitle, { TileSize } from "@repo/components/molecules/J2NTitle";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NAvatar from "@repo/components/atoms/J2NAvatar";
import J2NImage from "@repo/components/atoms/J2NImage";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import { IconDownload } from "@tabler/icons-react";

const AboutMePage = () => {
  return (
    <J2NSection name="about-me" className="bg-j2n-sand-500 p-6">
      <h1>about me</h1>
      <J2NTitle
        title="Frontend Layer"
        size={TileSize.xl}
        subTitle="(Frontend Developer)"
        divider
      />
      <J2NAvatar
        size={"175px"}
        src="https://scontent.fhan17-1.fna.fbcdn.net/v/t39.30808-6/597199448_122360319680004195_2578910024324556104_n.jpg?_nc_cat=102&ccb=1-7&_nc_sid=833d8c&_nc_eui2=AeEuYDeAh87at7_HxHk89rwLIyWAuvqobBEjJYC6-qhsEbSTlevtwQ6eZEsrqZSAizMqXtxEiLELE1LREdveXPN8&_nc_ohc=z2SA9lopvE0Q7kNvwEaOk6g&_nc_oc=AdkEeGRtB4p_J3SsDP7Jkd_inQt1xKEqHkFGQijqfDSANi0BsuMDD3Lw_UKPpliJt08&_nc_zt=23&_nc_ht=scontent.fhan17-1.fna&_nc_gid=L8iGssMXOixVVTN9g1w7-w&oh=00_Afkq3seLdsf8qoAZbcvNN5HCbI3_kD9wMjTCfQOzBMEYuQ&oe=6941AADC"
      />
      <J2NImage
        src="https://picsum.photos/id/237/200/300"
        alt="Avatar"
        width="250px"
        height="300px"
        classNames="m-2 p-2"
      />
      <J2NButton>Primary</J2NButton>
      <J2NButton j2nType={J2NButtonTypes.SECONDARY}>
        <IconDownload /> Secondary
      </J2NButton>
    </J2NSection>
  );
};

export default AboutMePage;
