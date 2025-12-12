import { Avatar, AvatarProps } from "@mantine/core";

interface J2NAvatarProps extends AvatarProps {
  src: string;
  isRounded?: boolean;
}

function J2NAvatar({ isRounded = true, ...props }: J2NAvatarProps) {
  if (!isRounded) {
    return <Avatar {...props} />;
  }
  return (
    <div className="avatar-wrapper w-fit p-1.5 border-2 border-j2n-sand-medium-300 bg-transparent rounded-full">
      <Avatar {...props} />
    </div>
  );
}

export default J2NAvatar;
