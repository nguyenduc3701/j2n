import { Avatar } from "@mantine/core";
import { J2NAvatarProps } from "./J2NAvatar.type";

function J2NAvatar({ isRounded = true, className, ...props }: J2NAvatarProps) {
  if (!isRounded) {
    return <Avatar {...props} />;
  }
  return (
    <div
      className={`avatar-wrapper w-fit p-1.5 border-3 border-j2n-mauve-500 bg-transparent rounded-full ${className}`}
    >
      <Avatar {...props} />
    </div>
  );
}

export default J2NAvatar;
