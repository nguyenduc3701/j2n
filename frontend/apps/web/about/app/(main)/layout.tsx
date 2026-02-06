import J2NHeader from "@repo/components/molecules/J2NHeader";

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <>
      <J2NHeader title="Jadon Nguyen" className="bg-j2n-sand-100!" />
      <main className="bg-j2n-sand-100">{children}</main>
    </>
  );
}
